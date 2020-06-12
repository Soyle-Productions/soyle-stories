package com.soyle.stories.scene.reorderScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene

class ReorderSceneNotifier : Notifier<ReorderScene.OutputPort>(), ReorderScene.OutputPort {
	override fun sceneReordered(response: ReorderScene.ResponseModel) {
		notifyAll { it.sceneReordered(response) }
	}

	override fun failedToReorderScene(failure: Exception) {
		notifyAll { it.failedToReorderScene(failure) }
	}
}