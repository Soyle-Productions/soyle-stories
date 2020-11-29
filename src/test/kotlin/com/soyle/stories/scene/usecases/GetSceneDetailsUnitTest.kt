package com.soyle.stories.scene.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.scene.charactersInScene
import com.soyle.stories.scene.doubles.LocaleDouble
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.common.IncludedCharacterInScene
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetailsUseCase
import com.soyle.stories.storyevent.makeStoryEvent
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class GetSceneDetailsUnitTest {

    private val scene = makeScene()
    private val storyEvent = makeStoryEvent(id = scene.storyEventId)
    private val location = Location(Location.Id(), scene.projectId, "Location ${str()}", "")

    private var result: Any? = null

    @Test
    fun `scene doesn't exist`() {
        whenSceneDetailsRequested()
        result.shouldBe(sceneDoesNotExist(scene.id.uuid))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `linked location is output`(expectLocation: Boolean) {
        val scene = if (!expectLocation) scene
        else scene.withLocationLinked(location.id)
        sceneRepository.givenScene(scene)
        whenSceneDetailsRequested()
        result.shouldBe(responseModel(expectLocation = expectLocation))
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `included characters are output`(includesCharacters: Boolean) {
        val scene = if (!includesCharacters) scene
        else scene.withCharacters(List(5) { makeCharacter() })
        sceneRepository.givenScene(scene)
        whenSceneDetailsRequested()
        result.shouldBe(responseModel(expectCharacters = includesCharacters))
    }

    @Test
    fun `characters previously set motivations`() {
        val characters = List(5) { makeCharacter() }
        val scene = scene.withCharacters(characters)
        characters.forEach {
            makeScene(projectId = scene.projectId).withCharacterIncluded(it)
                .withMotivationForCharacter(it.id, "Motivation ${str()}")
                .let(sceneRepository::givenScene)
        }
        sceneRepository.givenScene(scene)
        whenSceneDetailsRequested()
        result.shouldBe(responseModel(expectCharacters = true, expectInheritedMotivations = true))
    }

    @Test
    fun `Output no covered sections if no arc sections covered in scene`() {
        val characters = List(5) { makeCharacter() }
        val scene = scene.withCharacters(characters)
        sceneRepository.givenScene(scene)
        whenSceneDetailsRequested()
        result.shouldBe(responseModel(expectCharacters = true, expectCoveredArcSections = false))
    }

    @Test
    fun `Output covered sections if arc sections are covered in scene`() {
        val characters = List(5) { makeCharacter() }
        val scene = characters.fold(scene.withCharacters(characters)) { nextScene, character ->
            val arc = CharacterArc.planNewCharacterArc(
                character.id,
                Theme.Id(),
                "Character Arc ${str()}",
                CharacterArcTemplate(
                    listOf(
                        template("Template ${str()}", required = true, multiple = true),
                        template("Template ${str()}", required = true, multiple = false),
                        template("Template ${str()}", required = false, multiple = true),
                        template("Template ${str()}", required = false, multiple = false)
                    )
                )
            )
            val covered = arc.arcSections.random()
            characterArcRepository.givenCharacterArc(arc.withArcSectionsMapped {
                if (it.id == covered.id) it.withValue("Value ${str()}") else it
            })
            nextScene.withCharacterArcSectionCovered(
                covered
            )
        }
        sceneRepository.givenScene(scene)
        whenSceneDetailsRequested()
        result.shouldBe(responseModel(expectCharacters = true, expectCoveredArcSections = true))
    }

    private fun Scene.withCharacters(characters: List<Character>) = characters.fold(this) { scene, character ->
        scene.withCharacterIncluded(character)
    }

    private val sceneRepository = SceneRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble()

    private fun whenSceneDetailsRequested() {
        val useCase: GetSceneDetails = GetSceneDetailsUseCase(sceneRepository, characterArcRepository)
        val output = object : GetSceneDetails.OutputPort {
            override fun failedToGetSceneDetails(failure: Exception) {
                result = failure
            }

            override fun sceneDetailsRetrieved(response: GetSceneDetails.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(GetSceneDetails.RequestModel(scene.id.uuid, LocaleDouble()), output)
        }
    }

    private fun responseModel(
        expectLocation: Boolean = false, expectCharacters: Boolean = false, expectInheritedMotivations: Boolean = false,
        expectCoveredArcSections: Boolean = false
    ): (Any?) -> Unit = { actual ->
        actual as GetSceneDetails.ResponseModel
        assertEquals(scene.id.uuid, actual.sceneId)
        assertEquals(storyEvent.id.uuid, actual.storyEventId)

        if (expectLocation) assertEquals(location.id.uuid, actual.locationId)
        else assertNull(actual.locationId)

        val scene = sceneRepository.scenes.getValue(scene.id)

        if (expectCharacters) {
            val expectedCharacters = scene.includedCharacters.associateBy { it.characterId.uuid }
            assertEquals(
                expectedCharacters.keys,
                actual.characters.map(IncludedCharacterInScene::characterId).toSet()
            )
            actual.characters.forEach {
                val expectedCharacter = expectedCharacters.getValue(it.characterId)
                assertEquals(scene.id.uuid, it.sceneId)
                assertEquals(expectedCharacter.characterName, it.characterName)
                assertEquals(scene.getMotivationForCharacter(expectedCharacter.characterId)?.motivation, it.motivation)
                if (expectCoveredArcSections) {
                    val expectedArcSectionIds =
                        scene.getCoveredCharacterArcSectionsForCharacter(expectedCharacter.characterId)
                    it.coveredArcSections.size.mustEqual(
                        expectedArcSectionIds?.size ?: 0
                    ) { "Unexpected number of output covered arc sections." }
                    expectedArcSectionIds
                        ?.map { characterArcRepository.getCharacterArcSection(it)!! }
                        ?.forEach { expectedArcSection ->
                            val coveredArcSection =
                                it.coveredArcSections.find { it.arcSectionId == expectedArcSection.id.uuid }!!
                            coveredArcSection.arcSectionTemplateName.mustEqual(expectedArcSection.template.name)
                            coveredArcSection.arcSectionValue.mustEqual(expectedArcSection.value)
                            coveredArcSection.arcSectionTemplateAllowsMultiple.mustEqual(expectedArcSection.template.allowsMultiple)
                            val arc = runBlocking {
                                characterArcRepository.getCharacterArcByCharacterAndThemeId(
                                    expectedArcSection.characterId,
                                    expectedArcSection.themeId
                                )!!
                            }
                            coveredArcSection.characterArcId.mustEqual(arc.id.uuid)
                            coveredArcSection.characterArcName.mustEqual(arc.name)
                        }
                } else {
                    it.coveredArcSections.isEmpty().mustEqual(true) { "No covered arc sections should be in output." }
                }
            }
            if (expectInheritedMotivations) {
                val motivationSources = sceneRepository.scenes.values.filterNot { it.id == scene.id }
                    .associateBy { it.charactersInScene().single().characterId.uuid }
                actual.characters.forEach {
                    val motivationSource = motivationSources.getValue(it.characterId)
                    assertEquals(motivationSource.id.uuid, it.inheritedMotivation?.sceneId)
                    assertEquals(motivationSource.name.value, it.inheritedMotivation?.sceneName)
                    assertEquals(
                        motivationSource.charactersInScene().single().motivation!!,
                        it.inheritedMotivation?.motivation
                    )
                }
            }
        } else assertTrue(actual.characters.isEmpty())
    }
}