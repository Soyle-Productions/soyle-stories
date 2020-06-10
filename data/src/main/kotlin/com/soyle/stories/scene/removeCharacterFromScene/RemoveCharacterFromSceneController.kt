package com.soyle.stories.scene.removeCharacterFromScene

import com.soyle.stories.common.LocaleManager
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.usecases.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEvent

class RemoveCharacterFromSceneController(
  private val threadTransformer: ThreadTransformer,
  private val localeManager: LocaleManager,
  private val removeCharacterFromScene: RemoveCharacterFromScene,
  private val removeCharacterFromSceneOutputPort: RemoveCharacterFromScene.OutputPort
) : RemoveCharacterFromStoryEvent.OutputPort {

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

	override fun receiveRemoveCharacterFromStoryEventFailure(failure: StoryEventException) {}

}