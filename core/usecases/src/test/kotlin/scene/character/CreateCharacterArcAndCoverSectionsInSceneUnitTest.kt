package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.character.template
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterArcTemplateRepositoryDouble
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterArcTemplateSectionDoesNotExist
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.*
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.characterArcSectionCoveredByScene
import com.soyle.stories.usecase.scene.sceneDoesNotExist
import com.soyle.stories.usecase.storyevent.characterDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class CreateCharacterArcAndCoverSectionsInSceneUnitTest {

    // post-conditions
    private var result: Any? = null
    private var createdTheme: Theme? = null
    private var createdArc: CharacterArc? = null
    private var updatedScene: Scene? = null

    @Nested
    /**
     * Read the character arc template and get all the sections grouped by if they are required
     */
    inner class `List Character Arc Section Types that Will be Created` {

        @Test
        fun `Default Character Arc Template is empty`() {
            // when
            invoke()
            // then
            with(result as CharacterArcSectionTypes) {
                assert(requiredTypes.isEmpty()) { "result should be empty when character arc template is empty" }
                assert(additionalTypes.isEmpty()) { "result should be empty when character arc template is empty" }
            }
        }

        @Test
        fun `Default Character Arc Template has no Required Sections`() {
            // given
            characterArcTemplateRepository.givenDefaultTemplate(
                CharacterArcTemplate(List(6) {
                    template("Template ${str()}", false)
                })
            )
            // when
            invoke()
            // then
            with(result as CharacterArcSectionTypes) {
                assert(requiredTypes.isEmpty()) { "requiredTypes should be empty when character arc template has not required sections" }
                additionalTypes.size.mustEqual(6) { "All non-required sections should be output" }
            }
        }

        @Test
        fun `Default Character Arc Template has Required Sections`() {
            // given
            val requiredSections = List(6) {
                template("Template ${str()}", true)
            }
            val otherSections = List(6) {
                template("Template ${str()}", false)
            }
            characterArcTemplateRepository.givenDefaultTemplate(
                CharacterArcTemplate(otherSections + requiredSections)
            )
            // when
            invoke()
            // then
            with(result as CharacterArcSectionTypes) {
                requiredTypes.size.mustEqual(requiredSections.size) { "Result size should be the same number of required sections" }
                additionalTypes.size.mustEqual(otherSections.size) { "Result size should be the same number of other sections" }

                requiredSections.forEach { baseSection ->
                    val outputSection = requiredTypes.find { it.templateSectionId == baseSection.id.uuid }
                        ?: error("Missing $baseSection from output")

                    outputSection.name.mustEqual(baseSection.name) {
                        "Output template section name does not match expected name"
                    }
                }
                otherSections.forEach { baseSection ->
                    val outputSection = additionalTypes.find { it.templateSectionId == baseSection.id.uuid }
                        ?: error("Missing $baseSection from output")

                    outputSection.name.mustEqual(baseSection.name) {
                        "Output template section name does not match expected name"
                    }
                }
            }
        }

        fun invoke() {
            runBlocking {
                useCase.listCharacterArcSectionTypesForNewArc(output)
            }
        }

    }

    @Nested
    inner class `Create Character Arc and Cover Sections in Scene` {

        // Preconditions
        private val character = makeCharacter()
        private val scene = makeScene().withCharacterIncluded(character).scene

        // input
        private val inputName = nonBlankStr()

        @Test
        fun `Character must exist`() {
            // when
            val error = assertThrows<CharacterDoesNotExist> {
                invoke()
            }
            // then
            error shouldBe characterDoesNotExist(character.id.uuid)
        }

        @Test
        fun `Scene must exist`() {
            // given
            characterRepository.givenCharacter(character)
            // when
            val error = assertThrows<SceneDoesNotExist> {
                invoke()
            }
            // then
            error shouldBe sceneDoesNotExist(scene.id.uuid)
        }

        @Test
        fun `Theme should be created`() {
            // given
            characterRepository.givenCharacter(character)
            sceneRepository.givenScene(scene)
            // when
            invoke()
            // then
            with(createdTheme!!) {
                projectId.mustEqual(character.projectId)
                name.mustEqual(inputName)
                with(getMajorCharacterById(character.id)!!) {
                    name.mustEqual(character.name.value)
                }
            }
            (result as CreateCharacterArcAndCoverSectionsInScene.ResponseModel).createdTheme.run {
                projectId.mustEqual(character.projectId.uuid)
                themeId.mustEqual(createdTheme!!.id.uuid)
                themeName.mustEqual(inputName)
            }
        }

        @Test
        fun `Character arc should be created`() {
            // given
            characterRepository.givenCharacter(character)
            sceneRepository.givenScene(scene)
            // when
            invoke()
            // then
            with(createdArc!!) {
                characterId.mustEqual(character.id)
                themeId.mustEqual(createdTheme!!.id)
                name.mustEqual(inputName)
            }
            (result as CreateCharacterArcAndCoverSectionsInScene.ResponseModel).createdCharacterArc.run {
                characterId.mustEqual(character.id.uuid)
                themeId.mustEqual(createdTheme!!.id.uuid)
                characterArcName.mustEqual(inputName)
            }
        }

        @ParameterizedTest
        @ValueSource(ints = [0, 6])
        fun `Character arc should contain all required template sections`(requiredTemplateCount: Int) {
            // given
            characterArcTemplateRepository.givenDefaultTemplate(CharacterArcTemplate(List(requiredTemplateCount) { template(str()) }))
            characterRepository.givenCharacter(character)
            sceneRepository.givenScene(scene)
            // when
            invoke()
            // then
            with(createdArc!!) {
                arcSections.size.mustEqual(requiredTemplateCount)
            }
        }

        @Test
        fun `Character arc template sections must exist`() {
            // given
            val existingSections = List(5) { template("Template ${str()}") }
            val nonExistentId = UUID.randomUUID()
            val inputSectionIds = existingSections.map { it.id.uuid } + nonExistentId
            characterArcTemplateRepository.givenDefaultTemplate(CharacterArcTemplate(existingSections))
            characterRepository.givenCharacter(character)
            sceneRepository.givenScene(scene)
            // when
            val error = assertThrows<CharacterArcTemplateSectionDoesNotExist> {
                invoke(arcTemplateSectionsToCover = inputSectionIds.shuffled())
            }
            // then
            with(error) {
                characterArcTemplateSectionId.mustEqual(nonExistentId)
            }
        }

        @Test
        fun `Scene should cover newly created character arc sections with requested templates`() {
            // given
            val existingSections = List(6) { template("Template ${str()}") }
            val inputSectionIds = existingSections.map { it.id.uuid }.shuffled().take(3)
            characterArcTemplateRepository.givenDefaultTemplate(CharacterArcTemplate(existingSections))
            characterRepository.givenCharacter(character)
            sceneRepository.givenScene(scene)
            // when
            invoke(arcTemplateSectionsToCover = inputSectionIds)
            // then
            val arc = createdArc!!
            val expectedCoveredSections = arc.arcSections.filter { it.template.id.uuid in inputSectionIds }
            with(updatedScene!!) {
                assert(isSameEntityAs(scene))
                expectedCoveredSections.forEach {
                    assertTrue(isCharacterArcSectionCovered(it.id)) {
                        "Newly created character arc section with template that was requested to be covered was not covered by the scene"
                    }
                }
            }
            (result as CreateCharacterArcAndCoverSectionsInScene.ResponseModel).sectionsCoveredByScene.run {
                size.mustEqual(expectedCoveredSections.size) { "Different number of covered sections in output that expected" }
                map { it.characterArcSectionId }.toSet().mustEqual(expectedCoveredSections.map { it.id.uuid }.toSet())
                forEach {
                    it.sceneId.mustEqual(scene.id.uuid) { "sceneId of CharacterArcSectionCoveredByScene is incorrect" }
                    it.characterId.mustEqual(character.id.uuid) { "characterId of CharacterArcSectionCoveredByScene is incorrect" }
                    it.themeId.mustEqual(createdTheme!!.id.uuid) { "themeId of CharacterArcSectionCoveredByScene is incorrect" }
                    val baseSection = expectedCoveredSections.find { section -> section.id.uuid == it.characterArcSectionId }!!
                    it shouldBe characterArcSectionCoveredByScene(baseSection, arc, scene.id.uuid)
                }
            }
        }

        @Test
        fun `Non-required template sections that are requested should be created and covered`() {
            // given
            val nonRequiredSections = List(6) { template("Template ${str()}", false) }
            val requiredSections = List(6) { template("Template ${str()}") }
            val existingSections = nonRequiredSections + requiredSections
            val inputSectionIds = nonRequiredSections.map { it.id.uuid }.take(3)
            characterArcTemplateRepository.givenDefaultTemplate(CharacterArcTemplate(existingSections))
            characterRepository.givenCharacter(character)
            sceneRepository.givenScene(scene)
            // when
            invoke(arcTemplateSectionsToCover = inputSectionIds)
            // then
            val arc = createdArc!!
            with(arc) {
                val requiredIds = requiredSections.map { it.id.uuid }.toSet()
                arcSections.size.mustEqual(requiredSections.size + inputSectionIds.filter { it !in requiredIds }.size)
            }
            val expectedCoveredSections = arc.arcSections.filter { it.template.id.uuid in inputSectionIds }
            with(updatedScene!!) {
                assert(isSameEntityAs(scene))
                expectedCoveredSections.forEach {
                    assertTrue(isCharacterArcSectionCovered(it.id)) {
                        "Newly created character arc section with template that was requested to be covered was not covered by the scene"
                    }
                }
            }
            (result as CreateCharacterArcAndCoverSectionsInScene.ResponseModel).sectionsCoveredByScene.run {
                size.mustEqual(expectedCoveredSections.size) { "Different number of covered sections in output that expected" }
                map { it.characterArcSectionId }.toSet().mustEqual(expectedCoveredSections.map { it.id.uuid }.toSet())
                forEach {
                    it.sceneId.mustEqual(scene.id.uuid) { "sceneId of CharacterArcSectionCoveredByScene is incorrect" }
                    it.characterId.mustEqual(character.id.uuid) { "characterId of CharacterArcSectionCoveredByScene is incorrect" }
                    it.themeId.mustEqual(createdTheme!!.id.uuid) { "themeId of CharacterArcSectionCoveredByScene is incorrect" }
                    val baseSection = expectedCoveredSections.find { section -> section.id.uuid == it.characterArcSectionId }!!
                    it shouldBe characterArcSectionCoveredByScene(baseSection, arc, scene.id.uuid)
                }
            }
        }

        fun invoke(name: NonBlankString = inputName, arcTemplateSectionsToCover: List<UUID> = listOf()) {
            runBlocking {
                useCase.invoke(
                    CreateCharacterArcAndCoverSectionsInScene.RequestModel(
                        character.id.uuid, scene.id.uuid, name,
                        coverSectionsWithTemplateIds = arcTemplateSectionsToCover
                    ),
                    output
                )
            }
        }

    }

    private val characterRepository = CharacterRepositoryDouble()
    private val characterArcRepository = CharacterArcRepositoryDouble(onAddNewCharacterArc = ::createdArc::set)
    private val characterArcTemplateRepository = CharacterArcTemplateRepositoryDouble()
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val themeRepository = ThemeRepositoryDouble(onAddTheme = ::createdTheme::set)

    private val useCase = CreateCharacterArcAndCoverSectionsInSceneUseCase(
        characterRepository,
        characterArcRepository,
        sceneRepository,
        themeRepository,
        characterArcTemplateRepository,
    )

    val output = object : CreateCharacterArcAndCoverSectionsInScene.OutputPort {
        override suspend fun receiveCharacterArcSectionTypes(response: CharacterArcSectionTypes) {
            result = response
        }

        override suspend fun characterArcCreatedAndSectionsCovered(response: CreateCharacterArcAndCoverSectionsInScene.ResponseModel) {
            result = response
        }
    }
}