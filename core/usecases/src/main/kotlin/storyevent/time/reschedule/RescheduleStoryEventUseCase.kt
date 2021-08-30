package com.soyle.stories.usecase.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class RescheduleStoryEventUseCase(
    private val storyEventRepository: StoryEventRepository
) : RescheduleStoryEvent {

    override suspend fun invoke(storyEventId: StoryEvent.Id, time: Long, output: RescheduleStoryEvent.OutputPort) {
        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
        val update = storyEvent.withTime(time)
        if (update is Successful) {
            storyEventRepository.updateStoryEvent(update.storyEvent)
            output.storyEventRescheduled(RescheduleStoryEvent.ResponseModel(update.change))
        }
    }
}