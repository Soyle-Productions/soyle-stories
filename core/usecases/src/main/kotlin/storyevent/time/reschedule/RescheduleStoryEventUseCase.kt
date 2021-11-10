package com.soyle.stories.usecase.storyevent.time.reschedule

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class RescheduleStoryEventUseCase(
    private val storyEventRepository: StoryEventRepository
) : RescheduleStoryEvent {

    override suspend fun invoke(storyEventId: StoryEvent.Id, time: Long, output: RescheduleStoryEvent.OutputPort) {
        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
        val updates = StoryEventTimeService(storyEventRepository)
            .rescheduleStoryEvent(storyEvent, time)
        val successfulUpdates = updates.filterIsInstance<Successful<StoryEventRescheduled>>()
        if (successfulUpdates.isNotEmpty()) {
            updates.forEach { storyEventRepository.updateStoryEvent(it.storyEvent) }
            output.storyEventRescheduled(RescheduleStoryEvent.ResponseModel(successfulUpdates.map { it.change }))
        }
    }
}