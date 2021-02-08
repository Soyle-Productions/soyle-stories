package com.soyle.stories.storyevent.createStoryEvent

import com.soyle.stories.domain.validation.NonBlankString

interface CreateStoryEventController {

	fun createStoryEvent(name: NonBlankString)

	fun createStoryEventBefore(name: NonBlankString, relativeStoryEventId: String)

	fun createStoryEventAfter(name: NonBlankString, relativeStoryEventId: String)

}