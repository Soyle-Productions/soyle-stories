package com.soyle.stories.usecase.scene.character.include

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository

class IncludeCharacterInSceneUseCase(
    private val scenes: SceneRepository,
    private val characters: CharacterRepository
) : IncludeCharacterInScene {

    private data class Preconditions(
        val scene: Scene,
        val character: Character
    )

    override suspend fun invoke(
        sceneId: Scene.Id,
        characterId: Character.Id,
        output: IncludeCharacterInScene.OutputPort
    ): Result<Unit> {
        return runCatching {
            Preconditions(
                scenes.getSceneOrError(sceneId.uuid),
                characters.getCharacterOrError(characterId.uuid)
            ).includeCharacterInScene(output)?.let { throw it }
        }
    }

    private suspend fun Preconditions.includeCharacterInScene(output: IncludeCharacterInScene.OutputPort): Throwable? {
        when (val update = scene.withCharacterIncluded(character)) {
            is SceneUpdate.UnSuccessful -> return update.reason
            is SceneUpdate.Successful -> {
                scenes.updateScene(update.scene)
                output.characterIncludedInScene(IncludeCharacterInScene.ResponseModel(update.event, character.displayName.value, null))
            }
        }
        return null
    }

}