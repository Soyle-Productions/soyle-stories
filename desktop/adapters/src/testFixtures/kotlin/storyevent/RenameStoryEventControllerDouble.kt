package com.soyle.stories.desktop.adapter.storyevent

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.storyevent.rename.RenameStoryEventController
import kotlinx.coroutines.Job

class RenameStoryEventControllerDouble : RenameStoryEventController {

    override fun requestToRenameStoryEvent(storyEventId: StoryEvent.Id, currentName: String) {

    }

    override fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString): Job {
        return Job()
    }
}