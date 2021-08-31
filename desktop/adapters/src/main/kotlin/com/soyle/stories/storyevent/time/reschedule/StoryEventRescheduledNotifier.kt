package com.soyle.stories.storyevent.time.reschedule

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled

class StoryEventRescheduledNotifier : Notifier<StoryEventRescheduledReceiver>(), StoryEventRescheduledReceiver {

    override suspend fun receiveStoryEventRescheduled(event: StoryEventRescheduled) {
        notifyAll { it.receiveStoryEventRescheduled(event) }
    }
}