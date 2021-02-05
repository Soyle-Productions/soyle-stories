package com.soyle.stories.scene.removeCharacterFromScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene

class RemoveCharacterFromSceneNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<RemoveCharacterFromScene.OutputPort>(), RemoveCharacterFromScene.OutputPort {

	override fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.characterRemovedFromScene(response) }
		}
	}

	override fun failedToRemoveCharacterFromScene(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.failedToRemoveCharacterFromScene(failure) }
		}
	}

}