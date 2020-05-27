package com.soyle.stories.scene.createNewScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent

class CreateNewSceneNotifier(
  override val createStoryEventOutputPort: CreateStoryEvent.OutputPort
) : CreateNewScene.OutputPort, Notifier<CreateNewScene.OutputPort>() {

	override fun receiveCreateNewSceneFailure(failure: Exception) {
		notifyAll { it.receiveCreateNewSceneFailure(failure) }
	}

	override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
		notifyAll { it.receiveCreateNewSceneResponse(response) }
	}
}