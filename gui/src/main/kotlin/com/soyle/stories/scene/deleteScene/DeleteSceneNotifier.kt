package com.soyle.stories.scene.deleteScene

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene

class DeleteSceneNotifier : DeleteScene.OutputPort, Notifier<DeleteScene.OutputPort>() {
	override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
		notifyAll { it.receiveDeleteSceneResponse(responseModel) }
	}

	override fun receiveDeleteSceneFailure(failure: SceneException) {
		notifyAll { it.receiveDeleteSceneFailure(failure) }
	}
}