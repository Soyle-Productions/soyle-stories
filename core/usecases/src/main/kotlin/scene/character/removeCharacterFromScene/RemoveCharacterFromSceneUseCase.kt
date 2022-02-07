package com.soyle.stories.usecase.scene.character.removeCharacterFromScene

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.SceneDoesNotIncludeCharacter
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneRepository

class RemoveCharacterFromSceneUseCase(
  private val scenes: SceneRepository
) : RemoveCharacterFromScene {

	override suspend fun invoke(
		sceneId: Scene.Id,
		characterId: Character.Id,
		output: RemoveCharacterFromScene.OutputPort
	) {
		val scene = scenes.getSceneOrError(sceneId.uuid)
		val ops = scene.withCharacter(characterId) ?: throw SceneDoesNotIncludeCharacter(sceneId, characterId)
		when(val update = ops.removed()) {
			is SceneUpdate.UnSuccessful -> {}
			is SceneUpdate.Successful -> {
				scenes.updateScene(update.scene)
				output.characterRemovedFromScene(RemoveCharacterFromScene.ResponseModel(update.change))
			}
		}

	}

}