package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.scene.character.removeCharacterFromScene.RemoveCharacterFromSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Remove Character from Scene Unit Test` {

 // Pre-Requisites
    /** the [scene] must exist */
    private val scene = makeScene()
    /** the [scene] must include the [character] */
    private val character = makeCharacter()

 // Post-Requisites
    /** the [scene] should be updated to no longer include the character */
    private var updatedScene: Scene? = null

 // Wiring

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

 // Tests

    @Nested
    inner class `Scene Must Exist` {

        @Test
        fun `given scene does not exist - should throw error`() {
            val result = removeCharacterFromScene()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
        }

    }

    @Nested
    inner class `Character Must be Included in Scene` {

        @Test
        fun `given character is not included in scene - should throw error`() {
            sceneRepository.givenScene(scene)

            val result = removeCharacterFromScene()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(SceneDoesNotIncludeCharacter(scene.id, character.id))
        }

    }

    @Test
    fun `should update scene to no longer include character`() {
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)

        removeCharacterFromScene()

        updatedScene!!.id.shouldBeEqualTo(scene.id)
        updatedScene!!.includesCharacter(character.id).shouldBeFalse()
    }

    @Test
    fun `should output character removed from scene event`() {
        sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)

        val result = removeCharacterFromScene()

        result.getOrThrow().characterRemoved.shouldBeEqualTo(CharacterRemovedFromScene(scene.id, character.id))
    }

    private fun removeCharacterFromScene(): Result<RemoveCharacterFromScene.ResponseModel>
    {
        val useCase = RemoveCharacterFromSceneUseCase(sceneRepository)
        var result = Result.failure<RemoveCharacterFromScene.ResponseModel>(Error("No response received"))
        return runBlocking {
            runCatching {
                useCase(scene.id, character.id) {
                    result = Result.success(it)
                }
            }
        }.mapCatching { result.getOrThrow() }
    }

}