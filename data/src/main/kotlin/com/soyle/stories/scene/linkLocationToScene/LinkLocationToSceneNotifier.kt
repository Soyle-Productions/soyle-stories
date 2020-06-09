package com.soyle.stories.scene.linkLocationToScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToScene

class LinkLocationToSceneNotifier : Notifier<LinkLocationToScene.OutputPort>(), LinkLocationToScene.OutputPort {

	override fun failedToLinkLocationToScene(failure: Exception) {
		notifyAll { it.failedToLinkLocationToScene(failure) }
	}

	override fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
		notifyAll { it.locationLinkedToScene(response) }
	}
}