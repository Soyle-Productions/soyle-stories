package com.soyle.stories.storyevent.create

import com.soyle.stories.domain.storyevent.events.StoryEventCreated

interface StoryEventCreatedReceiver {
    suspend fun receiveStoryEventCreated(event: StoryEventCreated)
}