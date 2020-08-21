package com.soyle.stories.scene.includeCharacterInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene

class IncludeCharacterInSceneNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<IncludeCharacterInScene.OutputPort>(), IncludeCharacterInScene.OutputPort {
	override fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.characterIncludedInScene(response) }
		}
	}

	override fun failedToIncludeCharacterInScene(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.failedToIncludeCharacterInScene(failure) }
		}
	}
}