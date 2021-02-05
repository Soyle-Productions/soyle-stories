package com.soyle.stories.scene.renameScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.renameScene.RenameScene

class RenameSceneNotifier(
	private val threadTransformer: ThreadTransformer
) : RenameScene.OutputPort, Notifier<RenameScene.OutputPort>() {
	override fun receiveRenameSceneResponse(response: RenameScene.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveRenameSceneResponse(response) }
		}
	}

	override fun receiveRenameSceneFailure(failure: SceneException) {
		threadTransformer.async {
			notifyAll { it.receiveRenameSceneFailure(failure) }
		}
	}
}