package com.soyle.stories.scene.reorder

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.reorderScene.ReorderScene

class ReorderSceneNotifier : Notifier<ReorderScene.OutputPort>(), ReorderScene.OutputPort {
	override suspend fun sceneReordered(response: ReorderScene.ResponseModel) {
		notifyAll { it.sceneReordered(response) }
	}
}