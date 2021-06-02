package com.soyle.stories.scene.deleteScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.hostedScene.HostedSceneRemovedReceiver
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene

class DeleteSceneOutput(
	private val threadTransformer: ThreadTransformer,
	private val sceneDeletedReceiver: SceneDeletedReceiver,
	private val hostedScenesRemovedReceiver: HostedSceneRemovedReceiver
) : DeleteScene.OutputPort {

	override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
		threadTransformer.async {
			sceneDeletedReceiver.receiveSceneDeleted(responseModel.sceneId.let(Scene::Id))
			hostedScenesRemovedReceiver.receiveHostedScenesRemoved(responseModel.hostedScenesRemoved)
		}
	}

	override fun receiveDeleteSceneFailure(failure: Exception) = throw failure

}
