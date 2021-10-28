package com.soyle.stories.desktop.adapter.storyevent

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.time.adjust.AdjustStoryEventsTimeController
import kotlinx.coroutines.Job

class AdjustStoryEventsTimeControllerDouble : AdjustStoryEventsTimeController {
    override fun requestToAdjustStoryEventsTimes(storyEventIds: Set<StoryEvent.Id>) {
    }
    override fun adjustStoryEventsTime(storyEventIds: Set<StoryEvent.Id>, amount: Long): Job {
        return Job()
    }
}