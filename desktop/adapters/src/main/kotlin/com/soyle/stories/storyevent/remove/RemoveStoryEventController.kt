package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import kotlinx.coroutines.Job

interface RemoveStoryEventController {

    fun removeStoryEvent(storyEventId: StoryEvent.Id): Job

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            removeStoryEventFromProject: RemoveStoryEventFromProject,
            output: RemoveStoryEventFromProject.OutputPort
        ) = object : RemoveStoryEventController {
            override fun removeStoryEvent(storyEventId: StoryEvent.Id):Job {
                return threadTransformer.async {
                    removeStoryEventFromProject.invoke(storyEventId, output)
                }
            }
        }
    }
}