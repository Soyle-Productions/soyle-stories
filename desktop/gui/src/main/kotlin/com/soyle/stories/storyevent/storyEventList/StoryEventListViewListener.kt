package com.soyle.stories.storyevent.storyEventList

import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString

interface StoryEventListViewListener {

	fun getValidState()
	fun renameStoryEvent(storyEventId: StoryEvent.Id, newName: NonBlankString)

}