package com.soyle.stories.scene.delete

import com.soyle.stories.location.hostedScene.HostedSceneRemovedReceiver
import com.soyle.stories.storyevent.coverage.StoryEventUncoveredBySceneReceiver
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene

class DeleteSceneOutput(
	private val sceneDeletedReceiver: SceneDeletedReceiver,
	private val hostedScenesRemovedReceiver: HostedSceneRemovedReceiver,
	private val storyEventUncoveredBySceneReceiver: StoryEventUncoveredBySceneReceiver
) : DeleteScene.OutputPort {

	override suspend fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
		sceneDeletedReceiver.receiveSceneDeleted(responseModel.sceneRemoved)
		hostedScenesRemovedReceiver.receiveHostedScenesRemoved(responseModel.hostedScenesRemoved)
		responseModel.storyEventUncovered?.let { storyEventUncoveredBySceneReceiver.receiveStoryEventUncoveredByScene(it) }
	}

}
