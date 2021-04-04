package com.soyle.stories.scene.charactersInScene.removeCharacterFromScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.charactersInScene.removeCharacterFromScene.RemoveCharacterFromScene

class RemovedCharacterFromSceneNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<RemovedCharacterFromSceneReceiver>(), RemovedCharacterFromSceneReceiver {

	override suspend fun receiveRemovedCharacterFromScene(removedCharacterFromScene: RemoveCharacterFromScene.ResponseModel) {
		notifyAll { it.receiveRemovedCharacterFromScene(removedCharacterFromScene) }
	}

}