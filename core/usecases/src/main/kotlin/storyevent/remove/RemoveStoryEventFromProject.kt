package com.soyle.stories.usecase.storyevent.remove

import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens

interface RemoveStoryEventFromProject {
    suspend operator fun invoke(storyEventId: StoryEvent.Id, output: OutputPort)

    class ResponseModel(
        val storyEventNoLongerHappens: StoryEventNoLongerHappens,
        val storyEventRemovedFromScene: StoryEventRemovedFromScene?
    )

    fun interface OutputPort {
        suspend fun storyEventRemovedFromProject(response: ResponseModel)
    }
}