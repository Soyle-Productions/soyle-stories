package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.CharacterIsNotMajorCharacterInTheme
import com.soyle.stories.domain.theme.CharacterNotInTheme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue.ChangedCharacterArcSectionValue
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.ChangeCharacterArcSectionValueAndCoverInScene
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.ChangeCharacterArcSectionValueAndCoverInSceneUseCase
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.characterIsNotMajorCharacterInTheme
import com.soyle.stories.usecase.theme.characterNotInTheme
import com.soyle.stories.usecase.theme.themeDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ChangeCharacterArcSectionValueAndCoverInSceneTest {

    // preconditions
    private val character = makeCharacter()
    private val theme = makeTheme()
        .withCharacterIncluded(character.id, character.name.value, character.media)
        .withCharacterPromoted(character.id)
    private val characterArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name)
    private val arcSection = characterArc.arcSections.random()
    private val scene = makeScene().withCharacterIncluded(character).scene

    // input
    private val themeId = theme.id.uuid
    private val characterId = character.id.uuid
    private val sceneId = scene.id.uuid
    private val providedValue = "New Value ${str()}"

    // post-conditions
    private var updatedCharacterArc: CharacterArc? = null
    private var updatedScene: Scene? = null

    // output
    private var responseModel: ChangeCharacterArcSectionValueAndCoverInScene.ResponseModel? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            changeCharacterArcSectionValueAndCoverInScene()
        } shouldBe themeDoesNotExist(themeId)
    }

    @Test
    fun `character not in theme`() {
        givenTheme()
        assertThrows<CharacterNotInTheme> {
            changeCharacterArcSectionValueAndCoverInScene()
        } shouldBe characterNotInTheme(themeId, characterId)
    }

    @Test
    fun `character is minor character`() {
        givenTheme()
        givenThemeHasCharacter()
        assertThrows<CharacterIsNotMajorCharacterInTheme> {
            changeCharacterArcSectionValueAndCoverInScene()
        } shouldBe characterIsNotMajorCharacterInTheme(themeId, characterId)
    }

    @Test
    fun `scene doesn't exist`() {
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        assertThrows<SceneDoesNotExist> {
            changeCharacterArcSectionValueAndCoverInScene()
        } shouldBe sceneDoesNotExist(sceneId)
    }

    @Test
    fun `happy path`() {
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        givenScene()
        changeCharacterArcSectionValueAndCoverInScene()
        updatedCharacterArc!!.arcSections.find { it.id == arcSection.id }!!.shouldBe {
            assertEquals(providedValue, it.value)
        }
        updatedScene!! shouldBe {
            assertTrue(it isSameEntityAs scene)
            assertTrue(it.isCharacterArcSectionCovered(arcSection.id))
        }
        responseModel!!.changedCharacterArcSectionValue shouldBe ::changedCharacterArcSectionValue
        responseModel!!.characterArcSectionCoveredByScene shouldBe ::characterArcSectionCoveredByScene
    }

    @Test
    fun `linked location set`() {
        val linkedLocationId = Location.Id()
        givenTheme()
        givenThemeHasCharacter(asMajorCharacter = true)
        givenScene()
        givenArcSectionHasLinkedLocation(linkedLocationId)
        changeCharacterArcSectionValueAndCoverInScene()
        updatedCharacterArc!!.arcSections.find { it.id == arcSection.id }!! shouldBe {
            assertEquals(providedValue, it.value)
            assertEquals(linkedLocationId, it.linkedLocation)
        }
        updatedScene!! shouldBe {
            assertTrue(it isSameEntityAs scene)
            assertTrue(it.isCharacterArcSectionCovered(arcSection.id))
        }
        responseModel!!.changedCharacterArcSectionValue shouldBe ::changedCharacterArcSectionValue
        responseModel!!.characterArcSectionCoveredByScene shouldBe ::characterArcSectionCoveredByScene
    }


    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedCharacterArc::set)
    private val themeRepository = ThemeRepositoryDouble()
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    private fun givenTheme() {
        themeRepository.themes[theme.id] = theme.withoutCharacter(character.id)
    }

    private fun givenThemeHasCharacter(asMajorCharacter: Boolean = false) {
        themeRepository.themes[theme.id] = if (asMajorCharacter) theme.also {
            characterArcRepository.givenCharacterArc(characterArc)
        } else {
            theme.withCharacterDemoted(theme.getMajorCharacterById(character.id)!!)
        }
    }
    private fun givenScene() {
        sceneRepository.givenScene(scene)
    }

    private fun givenArcSectionHasLinkedLocation(locationId: Location.Id) {
        runBlocking {
            characterArcRepository.givenCharacterArc(
                characterArcRepository.getCharacterArcByCharacterAndThemeId(character.id, theme.id)!!
                    .withArcSectionsMapped {
                        if (it.id == arcSection.id) it.withLinkedLocation(locationId)
                        else it
                    }
            )
        }
    }

    private fun changeCharacterArcSectionValueAndCoverInScene() {
        val useCase: ChangeCharacterArcSectionValueAndCoverInScene =
            ChangeCharacterArcSectionValueAndCoverInSceneUseCase(themeRepository, characterArcRepository, sceneRepository)
        val output = object : ChangeCharacterArcSectionValueAndCoverInScene.OutputPort {
            override suspend fun characterArcSectionValueChangedAndAddedToScene(response: ChangeCharacterArcSectionValueAndCoverInScene.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(ChangeCharacterArcSectionValueAndCoverInScene.RequestModel(themeId, characterId, arcSection.id.uuid, sceneId, providedValue), output)
        }
    }

    private fun changedCharacterArcSectionValue(it: ChangedCharacterArcSectionValue) {
        assertEquals(themeId, it.themeId)
        assertEquals(characterId, it.characterId)
        assertEquals(arcSection.id.uuid, it.arcSectionId)
        assertNull(it.type)
        assertEquals(providedValue, it.newValue)
    }

    private fun characterArcSectionCoveredByScene(actual: CharacterArcSectionCoveredByScene)
    {
        assertEquals(sceneId, actual.sceneId)
        assertEquals(characterId, actual.characterId)
        assertEquals(themeId, actual.themeId)
        assertEquals(characterArc.id.uuid, actual.characterArcId)
        assertEquals(characterArc.name, actual.characterArcName)
        assertEquals(arcSection.id.uuid, actual.characterArcSectionId)
        assertEquals(arcSection.template.name, actual.characterArcSectionName)
        assertEquals(arcSection.template.allowsMultiple, actual.isMultiTemplate)
        assertEquals(providedValue, actual.characterArcSectionValue)
    }
}