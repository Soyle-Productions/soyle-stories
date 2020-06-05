package com.soyle.stories.scene.includeCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene

class IncludeCharacterInSceneNotifier : Notifier<IncludeCharacterInScene.OutputPort>(), IncludeCharacterInScene.OutputPort {
	override fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
		notifyAll { it.characterIncludedInScene(response) }
	}

	override fun failedToIncludeCharacterInScene(failure: Exception) {
		notifyAll { it.failedToIncludeCharacterInScene(failure) }
	}
}