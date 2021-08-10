package com.soyle.stories.storyevent.create

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.events.StoryEventCreated

class StoryEventCreatedNotifier : Notifier<StoryEventCreatedReceiver>(), StoryEventCreatedReceiver {

    override suspend fun receiveStoryEventCreated(event: StoryEventCreated) {
        notifyAll { it.receiveStoryEventCreated(event) }
    }

}