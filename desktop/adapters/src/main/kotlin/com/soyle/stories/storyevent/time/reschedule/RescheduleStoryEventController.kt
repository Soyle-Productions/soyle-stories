package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEvent
import kotlinx.coroutines.Job

interface RescheduleStoryEventController {

    fun rescheduleStoryEvent(storyEventId: StoryEvent.Id, time: Long): Job

    companion object {

        operator fun invoke(
            threadTransformer: ThreadTransformer,
            rescheduleStoryEvent: RescheduleStoryEvent,
            rescheduleStoryEventOutput: RescheduleStoryEvent.OutputPort
        ): RescheduleStoryEventController = object : RescheduleStoryEventController {
            override fun rescheduleStoryEvent(storyEventId: StoryEvent.Id, time: Long): Job {
                return threadTransformer.async {
                    rescheduleStoryEvent.invoke(storyEventId, time, rescheduleStoryEventOutput)
                }
            }
        }
    }

}