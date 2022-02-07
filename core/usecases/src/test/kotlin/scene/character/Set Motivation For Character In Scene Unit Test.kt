package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.character.events.CharacterGainedMotivationInScene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.exceptions.CharacterInSceneAlreadyDoesNotHaveMotivation
import com.soyle.stories.domain.scene.givenCharacter
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.character.InvolvedCharacter
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import com.soyle.stories.usecase.scene.character.setMotivationForCharacterInScene.SetMotivationForCharacterInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Set Motivation For Character In Scene Unit Test` {

    private val scene = makeScene()
    private val character = makeCharacter()

    private var updatedScene: Scene? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val storyEventRepository = StoryEventRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble(initialCharacters = listOf(character))

    @Test
    fun `should throw error if scene doesn't exist`() {
        val error = setCharacterMotivation().exceptionOrNull()

        error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
    }

    @Nested
    inner class `Character Must Exist` {

        init {
            sceneRepository.givenScene(scene)
            characterRepository.characters.remove(character.id)
        }

        @Test
        fun `given character does not exist - should throw error`() {
            val result = setCharacterMotivation()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(CharacterDoesNotExist(character.id))
        }

    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should throw error when character not in scene`() {
            val error = setCharacterMotivation().exceptionOrNull()

            error.shouldBeEqualTo(SceneDoesNotIncludeCharacter(scene.id, character.id))
        }

        @Test
        fun `character not included, but covered story event involves character`() {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(InvolvedCharacter(character.id, "")))
                .also(storyEventRepository::givenStoryEvent)

            val result = setCharacterMotivation("Some Motivation")

            val response = result.getOrThrow()
            response.characterIncludedInScene.shouldBeEqualTo(CharacterIncludedInScene(scene.id, character.id, character.displayName.value, scene.projectId))
            response.characterMotivationInSceneChanged.shouldBeEqualTo(
                CharacterGainedMotivationInScene(scene.id, character.id, "Some Motivation")
            )
        }

        @Test
        fun `character involved in covered story event and motivation is null`() {
            makeStoryEvent(sceneId = scene.id, includedCharacterIds = entitySetOf(InvolvedCharacter(character.id, "")))
                .also(storyEventRepository::givenStoryEvent)

            val result = setCharacterMotivation(null)

            val error = result.exceptionOrNull()
            error.shouldBeEqualTo(CharacterInSceneAlreadyDoesNotHaveMotivation(scene.id, character.id))
            updatedScene.shouldBeNull()
        }

        @Nested
        inner class `Given Scene Includes Character` {

            private val storyEvent = makeStoryEvent().withCharacterInvolved(character)
                .storyEvent
            private val sceneWithCharacter = scene.givenCharacter(character).also(sceneRepository::givenScene)

            @Test
            fun `should update scene with new motivation for character`() {
                setCharacterMotivation("New Motivation")

                with(updatedScene!!) {
                    id.mustEqual(scene.id)
                    includedCharacters.getOrError(character.id).motivation.mustEqual("New Motivation")
                }
            }

            @Test
            fun `should output event`() {
                val result = setCharacterMotivation("New Motivation")

                result.getOrThrow().characterMotivationInSceneChanged.shouldBeEqualTo(
                    CharacterGainedMotivationInScene(scene.id, character.id, "New Motivation")
                )
            }

            @Nested
            inner class `Given Character Already has Motivation` {

                init {
                    sceneWithCharacter.withCharacter(character.id)!!.motivationChanged("New Motivation")
                        .scene.also(sceneRepository::givenScene)
                }

                @Test
                fun `should not update scene`() {
                    setCharacterMotivation("New Motivation")

                    assertNull(updatedScene)
                }

                @Test
                fun `should not produce output`() {
                    val result = setCharacterMotivation("New Motivation")

                    assertNull(result.getOrNull())
                }

            }

        }

    }

    private fun setCharacterMotivation(motivation: String? = "Some Motivation"): Result<SetMotivationForCharacterInScene.ResponseModel> {
        val useCase: SetMotivationForCharacterInScene =
            SetMotivationForCharacterInSceneUseCase(sceneRepository, storyEventRepository, characterRepository)
        val request = SetMotivationForCharacterInScene.RequestModel(
            scene.id,
            character.id,
            motivation
        )
        var result = Result.failure<SetMotivationForCharacterInScene.ResponseModel>(Error("No response received"))
        return runBlocking {
            runCatching {
                useCase(request) {
                    result = Result.success(it)
                }
            }
        }.mapCatching { result.getOrThrow() }
    }
}