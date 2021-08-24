package com.soyle.stories.storyevent.rename

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed

class StoryEventRenamedNotifier : Notifier<StoryEventRenamedReceiver>(), StoryEventRenamedReceiver {

    override suspend fun receiveStoryEventRenamed(event: StoryEventRenamed) {
        notifyAll { it.receiveStoryEventRenamed(event) }
    }
}