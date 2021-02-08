package com.soyle.stories.storyevent.createStoryEventDialog

import com.soyle.stories.domain.validation.NonBlankString

interface CreateStoryEventDialogViewListener {

	fun getValidState()
	fun createStoryEvent(name: NonBlankString, relativeStoryEventId: String?, relativePosition: String?)

}