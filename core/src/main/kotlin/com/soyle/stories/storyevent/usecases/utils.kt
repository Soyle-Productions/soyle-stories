package com.soyle.stories.storyevent.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import java.util.*

internal suspend fun getOrderOfEventsInProject(storyEventRepository: StoryEventRepository, projectId: UUID): List<StoryEvent>
{
	val events = storyEventRepository.listStoryEventsInProject(Project.Id(projectId))
	val firstEvent = events.find { it.previousStoryEventId == null } ?: return emptyList()
	val eventMap = events.associateBy { it.id }

	val orderedEvents = ArrayList<StoryEvent>(events.size)
	var currentEvent = firstEvent
	repeat(events.size) {
		orderedEvents.add(currentEvent)
		currentEvent.nextStoryEventId?.let {
			currentEvent = eventMap.getValue(it)
		}
	}
	return orderedEvents
}