package com.soyle.stories.scene.linkLocationToScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToScene

class LinkLocationToSceneNotifier(
	private val threadTransformer: ThreadTransformer
) : Notifier<LinkLocationToScene.OutputPort>(), LinkLocationToScene.OutputPort {

	override fun failedToLinkLocationToScene(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.failedToLinkLocationToScene(failure) }
		}
	}

	override fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.locationLinkedToScene(response) }
		}
	}
}