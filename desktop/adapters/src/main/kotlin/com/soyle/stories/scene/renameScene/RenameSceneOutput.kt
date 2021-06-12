package com.soyle.stories.scene.renameScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.hostedScene.HostedSceneRenamedReceiver
import com.soyle.stories.usecase.scene.renameScene.RenameScene

class RenameSceneOutput(
	private val sceneRenamedReceiver: SceneRenamedReceiver,
	private val hostedScenesRenamedReceiver: HostedSceneRenamedReceiver
) : RenameScene.OutputPort {

	override suspend fun receiveRenameSceneResponse(response: RenameScene.ResponseModel) {
		response.sceneRenamed?.let { sceneRenamedReceiver.receiveSceneRenamed(it) }
		hostedScenesRenamedReceiver.receiveHostedScenesRenamed(response.hostedScenesRenamed)
	}

	override suspend fun receiveRenameSceneFailure(failure: Exception) = throw failure
}
