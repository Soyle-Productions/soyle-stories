package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.events.CharacterDesireInSceneChanged
import com.soyle.stories.domain.scene.givenCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.character.InvolvedCharacter
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.exceptions.scene.assertThrowsSceneDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInScene
import com.soyle.stories.usecase.scene.character.setDesire.SetCharacterDesireInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Set Character Desire in Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    private var updatedScene: Scene? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble(initialCharacters = listOf(character))

    @Test
    fun `scene does not exist`() {
        assertThrowsSceneDoesNotExist(scene.id) {
            setCharacterDesireInScene("")
        }
        Assertions.assertNull(updatedScene)
    }

    @Nested
    inner class `Character Must Exist` {

        init {
            sceneRepository.givenScene(scene)
            characterRepository.characters.remove(character.id)
        }

        @Test
        fun `given character does not exist - should throw error`() {
            val error = assertThrows<CharacterDoesNotExist> { setCharacterDesireInScene("") }
            error.shouldBeEqualTo(CharacterDoesNotExist(character.id))
        }

    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `when scene doesn't include character, should throw error`() {
            val error = assertThrows<SceneDoesNotIncludeCharacter> {
                setCharacterDesireInScene("")
            }
            error.characterId.mustEqual(character.id)
            error.sceneId.mustEqual(scene.id)
        }

        @Test
        fun `character not included, but covered story event involves character`() {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(InvolvedCharacter(character.id, "")))
                .also(storyEventRepository::givenStoryEvent)

            val result = runCatching { setCharacterDesireInScene("Some desire") }

            val response = result.getOrThrow()
            response.characterIncludedInScene.shouldBeEqualTo(CharacterIncludedInScene(scene.id, character.id, character.displayName.value, scene.projectId))
            response.characterDesireInSceneChanged.shouldBeEqualTo(
                CharacterDesireInSceneChanged(scene.id, character.id, "Some desire")
            )
        }

        @Nested
        inner class `Given Scene Includes Character` {

            private val sceneWithCharacter = scene.givenCharacter(character).also(sceneRepository::givenScene)

            @Test
            fun `should produce character desire changed event`() {
                val result = setCharacterDesireInScene("Get dat bread")

                with(result.characterDesireInSceneChanged) {
                    characterId.mustEqual(character.id)
                    sceneId.mustEqual(scene.id)
                    newDesire.mustEqual("Get dat bread")
                }
            }

            @Test
            fun `should update scene with new desire for character`() {
                setCharacterDesireInScene("Get dat bread")

                with(updatedScene!!) {
                    id.mustEqual(scene.id)
                    includedCharacters.getOrError(character.id).desire.mustEqual("Get dat bread")
                }
            }

            @Nested
            inner class `Given Desire is Already Set to Input Value` {

                private val sceneWithDesireSet = sceneWithCharacter.withCharacter(character.id)!!.desireChanged("Get dat bread")
                    .scene.also(sceneRepository::givenScene)

                @Test
                fun `should not produce update`() {
                    val result = kotlin.runCatching { setCharacterDesireInScene("Get dat bread") }

                    val response = result.getOrNull()
                    assertNull(response)
                    assertNull(updatedScene)
                }

            }

        }

    }

    private fun setCharacterDesireInScene(desire: String): SetCharacterDesireInScene.ResponseModel {
        val useCase: SetCharacterDesireInScene = SetCharacterDesireInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
        val request = SetCharacterDesireInScene.RequestModel(scene.id, character.id, desire)
        lateinit var result: SetCharacterDesireInScene.ResponseModel
        runBlocking {
            useCase(request) {
                result = it
            }
        }
        return result
    }


}