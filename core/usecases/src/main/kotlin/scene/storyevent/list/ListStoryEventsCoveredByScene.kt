package com.soyle.stories.usecase.scene.storyevent.list

import com.soyle.stories.domain.scene.Scene

interface ListStoryEventsCoveredByScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    fun interface OutputPort {
        suspend fun receiveStoryEventsCoveredByScene(storyEventsInScene: StoryEventsInScene)
    }
}