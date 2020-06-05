package com.soyle.stories.scene.includeCharacterInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent

class IncludeCharacterInSceneController(
  private val threadTransformer: ThreadTransformer,
  private val includeCharacterInScene: IncludeCharacterInScene,
  private val includeCharacterInSceneOutputPort: IncludeCharacterInScene.OutputPort
) : AddCharacterToStoryEvent.OutputPort {

	override fun receiveAddCharacterToStoryEventResponse(response: AddCharacterToStoryEvent.ResponseModel) {
		threadTransformer.async {
			includeCharacterInScene.invoke(response, includeCharacterInSceneOutputPort)
		}
	}

	override fun receiveAddCharacterToStoryEventFailure(failure: Exception) {}
}