package com.soyle.stories.storyevent.rename

import com.soyle.stories.domain.storyevent.StoryEvent

interface RenameStoryEventPrompt {
    fun promptForNewName(storyEventId: StoryEvent.Id, currentName: String)
}