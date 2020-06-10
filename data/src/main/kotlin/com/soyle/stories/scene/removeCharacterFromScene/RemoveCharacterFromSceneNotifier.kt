package com.soyle.stories.scene.removeCharacterFromScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene

class RemoveCharacterFromSceneNotifier : Notifier<RemoveCharacterFromScene.OutputPort>(), RemoveCharacterFromScene.OutputPort {

	override fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
		notifyAll { it.characterRemovedFromScene(response) }
	}

	override fun failedToRemoveCharacterFromScene(failure: Exception) {
		notifyAll { it.failedToRemoveCharacterFromScene(failure) }
	}

}