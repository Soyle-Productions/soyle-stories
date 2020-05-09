package com.soyle.stories.scene.renameScene

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.usecases.renameScene.RenameScene

class RenameSceneNotifier : RenameScene.OutputPort, Notifier<RenameScene.OutputPort>() {
	override fun receiveRenameSceneResponse(response: RenameScene.ResponseModel) {
		notifyAll { it.receiveRenameSceneResponse(response) }
	}

	override fun receiveRenameSceneFailure(failure: SceneException) {
		notifyAll { it.receiveRenameSceneFailure(failure) }
	}
}