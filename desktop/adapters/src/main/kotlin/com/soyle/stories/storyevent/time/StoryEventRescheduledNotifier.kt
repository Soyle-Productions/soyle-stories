package com.soyle.stories.storyevent.time

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled

class StoryEventRescheduledNotifier : Notifier<StoryEventRescheduledReceiver>(), StoryEventRescheduledReceiver {

    override suspend fun receiveStoryEventsRescheduled(events: List<StoryEventRescheduled>) {
        notifyAll { it.receiveStoryEventsRescheduled(events) }
    }
}