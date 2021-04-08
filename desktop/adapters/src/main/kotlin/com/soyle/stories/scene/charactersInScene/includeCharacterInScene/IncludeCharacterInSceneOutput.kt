package com.soyle.stories.scene.charactersInScene.includeCharacterInScene

import com.soyle.stories.usecase.scene.character.includeCharacterInScene.IncludeCharacterInScene
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