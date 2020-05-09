package com.soyle.stories.scene.createNewScene

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene

class CreateNewSceneNotifier : CreateNewScene.OutputPort, Notifier<CreateNewScene.OutputPort>() {
	override fun receiveCreateNewSceneFailure(failure: SceneException) {
		notifyAll { it.receiveCreateNewSceneFailure(failure) }
	}

	override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
		notifyAll { it.receiveCreateNewSceneResponse(response) }
	}
}