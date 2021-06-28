package com.soyle.stories.scene.deleteScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene

class DeleteSceneNotifier(
	private val threadTransformer: ThreadTransformer
) : DeleteScene.OutputPort, Notifier<DeleteScene.OutputPort>() {
	override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveDeleteSceneResponse(responseModel) }
		}
	}

	override fun receiveDeleteSceneFailure(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.receiveDeleteSceneFailure(failure) }
		}
	}
}