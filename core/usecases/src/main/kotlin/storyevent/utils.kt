package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
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