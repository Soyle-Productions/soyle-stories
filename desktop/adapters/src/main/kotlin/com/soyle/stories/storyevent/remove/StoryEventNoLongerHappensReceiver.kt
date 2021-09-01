package com.soyle.stories.storyevent.remove

import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens

interface StoryEventNoLongerHappensReceiver {
    suspend fun receiveStoryEventNoLongerHappens(event: StoryEventNoLongerHappens)
}