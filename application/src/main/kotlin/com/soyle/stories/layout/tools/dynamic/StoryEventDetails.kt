package com.soyle.stories.layout.tools.dynamic

import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import java.util.*

data class StoryEventDetails(val storyEventId: UUID) : DynamicTool() {

	override val isTemporary: Boolean
		get() = false

	override suspend fun validate(context: OpenToolContext) {
		context.storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		  ?: throw StoryEventDoesNotExist(storyEventId)
	}

	override fun identifiedWithId(id: UUID): Boolean =
	  id == storyEventId
}