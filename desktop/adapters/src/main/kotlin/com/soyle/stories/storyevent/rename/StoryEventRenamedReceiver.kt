package com.soyle.stories.storyevent.rename

import com.soyle.stories.domain.storyevent.events.StoryEventRenamed

interface StoryEventRenamedReceiver {
    suspend fun receiveStoryEventRenamed(event: StoryEventRenamed)
}