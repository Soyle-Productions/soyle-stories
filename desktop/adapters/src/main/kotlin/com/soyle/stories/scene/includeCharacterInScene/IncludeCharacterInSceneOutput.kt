package com.soyle.stories.scene.includeCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene
import com.soyle.stories.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEventReceiver

class IncludeCharacterInSceneOutput(
	private val includedCharacterInSceneReceiver: IncludedCharacterInSceneReceiver,
	private val includedCharacterInStoryEventReceiver: IncludedCharacterInStoryEventReceiver
) : IncludeCharacterInScene.OutputPort {

	override suspend fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
		includedCharacterInSceneReceiver.receiveIncludedCharacterInScene(response.includedCharacterInScene)
		response.includedCharacterInStoryEvent?.let {
			includedCharacterInStoryEventReceiver.receiveIncludedCharacterInStoryEvent(it)
		}
	}
}