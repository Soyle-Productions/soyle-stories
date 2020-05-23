package com.soyle.stories.storyevent.usecases

import com.soyle.stories.storyevent.StoryEventCannotBeBlank

internal fun validateStoryEventName(name: String)
{
	if (name.isBlank()) throw StoryEventCannotBeBlank()
}