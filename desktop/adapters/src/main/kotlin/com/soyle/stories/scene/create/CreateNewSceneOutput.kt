package com.soyle.stories.scene.create

import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene

class CreateNewSceneOutput(
	private val sceneCreatedReceiver: SceneCreatedReceiver,
	private val storyEventCreatedReceiver: StoryEventCreatedReceiver
) : CreateNewScene.OutputPort {

	override suspend fun newSceneCreated(response: CreateNewScene.ResponseModel) {
		sceneCreatedReceiver.receiveSceneCreated(response.sceneCreated, response.sceneOrderUpdated)
		storyEventCreatedReceiver.receiveStoryEventCreated(response.storyEventCreated)
	}
}