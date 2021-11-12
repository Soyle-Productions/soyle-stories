package com.soyle.stories.usecase.scene.storyevent.list

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.toItem

class ListStoryEventsCoveredBySceneUseCase(
    private val sceneRepository: SceneRepository,
    private val storyEventRepository: StoryEventRepository
) : ListStoryEventsCoveredByScene {
    override suspend fun invoke(sceneId: Scene.Id, output: ListStoryEventsCoveredByScene.OutputPort) {
        sceneRepository.getSceneOrError(sceneId.uuid)
        val storyEvents = storyEventRepository.getStoryEventsCoveredByScene(sceneId)

        output.receiveStoryEventsCoveredByScene(
            StoryEventsInScene(
                sceneId,
                storyEvents.map { it.toItem() }
            )
        )
    }
}