package com.soyle.stories.scene.reorderScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene

class ReorderSceneNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<ReorderScene.OutputPort>(), ReorderScene.OutputPort {
	override fun sceneReordered(response: ReorderScene.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.sceneReordered(response) }
		}
	}

	override fun failedToReorderScene(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.failedToReorderScene(failure) }
		}
	}
}