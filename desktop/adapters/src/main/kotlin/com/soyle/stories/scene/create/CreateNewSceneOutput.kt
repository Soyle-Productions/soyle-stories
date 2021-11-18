package com.soyle.stories.scene.create

import com.soyle.stories.common.Notifier
import com.soyle.stories.storyevent.create.StoryEventCreatedReceiver
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

class CreateNewSceneOutput(
	private val sceneCreatedReceiver: SceneCreatedReceiver,
	private val storyEventCreatedReceiver: StoryEventCreatedReceiver
) : CreateNewScene.OutputPort {

	override suspend fun newSceneCreated(response: CreateNewScene.ResponseModel) {
		sceneCreatedReceiver.receiveSceneCreated(response.sceneCreated)
		storyEventCreatedReceiver.receiveStoryEventCreated(response.storyEventCreated)
	}
}