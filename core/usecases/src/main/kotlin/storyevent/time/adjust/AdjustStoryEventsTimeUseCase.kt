package com.soyle.stories.usecase.storyevent.time.adjust

import arrow.core.toT
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.toEntitySet
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class AdjustStoryEventsTimeUseCase(
    private val storyEventRepository: StoryEventRepository
) : AdjustStoryEventsTime {

    override suspend fun invoke(storyEventIds: Set<StoryEvent.Id>, adjustment: Long, output: AdjustStoryEventsTime.OutputPort) {
        // An adjustment of 0 would not result in any changes, so we can avoid any expensive calls by returning here
        if (adjustment == 0L) return

        // get all the story events from the repo, perform the adjustment, then separate the
        // updated storyEvents and the rescheduled events into separate lists
        val entities = storyEventIds.map { storyEventRepository.getStoryEventOrError(it) }
            .toEntitySet()
        val (storyEvents, events) = StoryEventTimeService(storyEventRepository)
            .adjustStoryEventTimesBy(entities, adjustment)
            .asSequence()
            .filterIsInstance<Successful<StoryEventRescheduled>>()
            .map { it.storyEvent to it.change }
            .unzip()

        // update the story events and output the rescheduled events
        storyEventRepository.updateStoryEvents(*storyEvents.toTypedArray())
        output.adjustedTimesForStoryEvents(AdjustStoryEventsTime.ResponseModel(events))
    }

}