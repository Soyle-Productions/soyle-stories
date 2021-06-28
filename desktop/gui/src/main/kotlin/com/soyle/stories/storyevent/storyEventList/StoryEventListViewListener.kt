package com.soyle.stories.storyevent.storyEventList

interface StoryEventListViewListener {

	fun getValidState()
	fun openStoryEventDetails(storyEventId: String)
	fun renameStoryEvent(storyEventId: String, newName: String)

}