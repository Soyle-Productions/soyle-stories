package com.soyle.stories.storyevent.createStoryEventDialog

interface CreateStoryEventDialogViewListener {

	fun getValidState()
	fun createStoryEvent(name: String, relativeStoryEventId: String?, relativePosition: String?)

}