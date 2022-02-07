package com.soyle.stories.usecase.scene.character

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.scene.character.exceptions.CharacterAlreadyIncludedInScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.validation.SoyleStoriesException
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInScene
import com.soyle.stories.usecase.scene.character.include.IncludeCharacterInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Explicitly adds the [character] to the [scene] without relying on a covered story event involving the character first
 */
class `Include Character in Scene Unit Test` {

 // Pre-Conditions

    /** The [scene] must exist */
    private val scene = makeScene()

    /** The [character] must exist */
    private val character = makeCharacter()

 // Post-Conditions

    /** The [scene] must be updated to include the [character] */
    private var updatedScene: Scene? = null

 // Wiring

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val characterRepository = CharacterRepositoryDouble()

 // Tests

    @Nested
    inner class `Scene Must Exist` {

        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `when scene does not exist - should return error`() {
            val result = includeCharacterInScene()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(SceneDoesNotExist(scene.id.uuid))
        }

    }

    @Nested
    inner class `Character Must Exist` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `when character does not exist - should return error`() {
            val result = includeCharacterInScene()

            val error = result.exceptionOrNull()!!
            error.shouldBeEqualTo(CharacterDoesNotExist(character.id))
        }

    }

    @Test
    fun `should update scene`() {
        sceneRepository.givenScene(scene)
        characterRepository.givenCharacter(character)

        includeCharacterInScene()

        updatedScene!!.let { updatedScene ->
            updatedScene.id.shouldBeEqualTo(scene.id)
            updatedScene.includedCharacters.getOrError(character.id)
        }
    }

    @Test
    fun `should output character included response`() {
        sceneRepository.givenScene(scene)
        characterRepository.givenCharacter(character)

        val response = includeCharacterInScene().getOrThrow()

        response.shouldBeEqualTo(IncludeCharacterInScene.ResponseModel(
            CharacterIncludedInScene(scene.id, character.id, character.displayName.value, scene.projectId),
            character.displayName.value, null)
        )
    }

    @Nested
    inner class `Must Handle Domain Errors` {

        init {
            sceneRepository.givenScene(scene.withCharacterIncluded(character).scene)
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `when scene fails to update - should not update scene`() {
            includeCharacterInScene()

            updatedScene.shouldBeNull()
        }

        @Test
        fun `when scene fails to update - should output error`() {
            val error = includeCharacterInScene().exceptionOrNull()

            error as SoyleStoriesException
        }

    }

    private fun includeCharacterInScene(): Result<IncludeCharacterInScene.ResponseModel> = runBlocking {
        val useCase: IncludeCharacterInScene = IncludeCharacterInSceneUseCase(sceneRepository, characterRepository)
        var result: Result<IncludeCharacterInScene.ResponseModel> = Result.failure(Error("No response received"))
        useCase(scene.id, character.id) {
            result = Result.success(it)
        }.mapCatching { result.getOrThrow() }
    }

}