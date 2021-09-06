package com.soyle.stories.storyevent.create

import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent

interface CreateStoryEventPrompt {
    fun promptToCreateStoryEvent(relativeTo: CreateStoryEvent.RequestModel.RequestedStoryEventTime.Relative?)
}