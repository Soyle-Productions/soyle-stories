package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens

class StoryEventNoLongerHappensNotifier : Notifier<StoryEventNoLongerHappensReceiver>(),
    StoryEventNoLongerHappensReceiver {

    override suspend fun receiveStoryEventNoLongerHappens(event: StoryEventNoLongerHappens) {
        notifyAll { it.receiveStoryEventNoLongerHappens(event) }
    }
}