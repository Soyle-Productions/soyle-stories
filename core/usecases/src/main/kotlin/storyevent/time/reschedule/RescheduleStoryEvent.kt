package com.soyle.stories.usecase.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist

interface RescheduleStoryEvent {

    /**
     * @param storyEventId the id of the story event to reschedule
     * @param time the time unit to move the story event to
     *
     * @throws StoryEventDoesNotExist if the supplied story event does not exist in the project
     */
    operator suspend fun invoke(storyEventId: StoryEvent.Id, time: Long, output: OutputPort)

    class ResponseModel(val storyEventRescheduled: StoryEventRescheduled)

    fun interface OutputPort {
        suspend fun storyEventRescheduled(response: ResponseModel)
    }

}