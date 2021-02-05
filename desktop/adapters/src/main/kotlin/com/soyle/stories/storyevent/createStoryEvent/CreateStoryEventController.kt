package com.soyle.stories.storyevent.createStoryEvent

interface CreateStoryEventController {

	fun createStoryEvent(name: String)

	fun createStoryEventBefore(name: String, relativeStoryEventId: String)

	fun createStoryEventAfter(name: String, relativeStoryEventId: String)

}