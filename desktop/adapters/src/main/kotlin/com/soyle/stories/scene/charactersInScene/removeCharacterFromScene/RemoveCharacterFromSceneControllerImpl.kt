package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent
import kotlinx.coroutines.Job

class RemoveCharacterFromSceneControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val removeCharacterFromScene: RemoveCharacterFromScene,
  private val removeCharacterFromSceneOutputPort: RemoveCharacterFromScene.OutputPort
) : RemoveCharacterFromStoryEvent.OutputPort, RemoveCharacterFromSceneController {

	override fun removeCharacterFromScene(sceneId: Scene.Id, characterId: Character.Id): Job {
		return threadTransformer.async {
			removeCharacterFromScene.removeCharacterFromScene(
				sceneId.uuid,
				localeManager.getCurrentLocale(),
				characterId.uuid,
				removeCharacterFromSceneOutputPort
			)
		}
	}

	override fun receiveRemoveCharacterFromStoryEventResponse(response: RemoveCharacterFromStoryEvent.ResponseModel) {
		threadTransformer.async {
			removeCharacterFromScene.removeCharacterFromSceneWithStoryEventId(
			  response.storyEventId,
			  localeManager.getCurrentLocale(),
			  response.removedCharacterId,
			  removeCharacterFromSceneOutputPort
			)
		}
	}

	override fun receiveRemoveCharacterFromStoryEventFailure(failure: Exception) {}

}