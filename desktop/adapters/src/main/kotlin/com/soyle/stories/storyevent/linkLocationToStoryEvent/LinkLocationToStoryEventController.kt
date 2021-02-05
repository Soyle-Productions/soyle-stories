package com.soyle.stories.storyevent.linkLocationToStoryEvent

interface LinkLocationToStoryEventController {

	fun linkLocationToStoryEvent(storyEventId: String, locationId: String)
	fun unlinkLocationToStoryEvent(storyEventId: String)
}