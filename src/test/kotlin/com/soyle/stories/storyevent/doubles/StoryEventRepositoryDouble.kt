package com.soyle.stories.storyevent.doubles

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.repositories.StoryEventRepository

class StoryEventRepositoryDouble(
  initialStoryEvents: List<StoryEvent> = emptyList(),

  private val onAddNewStoryEvent: (StoryEvent) -> Unit = {},
  private val onUpdateStoryEvent: (StoryEvent) -> Unit = {}
) : StoryEventRepository {

	val storyEvents = initialStoryEvents.associateBy { it.id }.toMutableMap()

	override suspend fun addNewStoryEvent(storyEvent: StoryEvent) {
		storyEvents[storyEvent.id] = storyEvent
		onAddNewStoryEvent.invoke(storyEvent)
	}

	override suspend fun getStoryEventById(storyEventId: StoryEvent.Id): StoryEvent? =
	  storyEvents[storyEventId]

	override suspend fun listStoryEventsInProject(projectId: Project.Id): List<StoryEvent> = storyEvents.values.filter {
		it.projectId == projectId
	}

	override suspend fun getLastStoryEventInProject(projectId: Project.Id): StoryEvent? = storyEvents.values.find {
		it.nextStoryEventId == null && it.projectId == projectId
	}

	override suspend fun updateStoryEvent(storyEvent: StoryEvent) {
		storyEvents[storyEvent.id] = storyEvent
		onUpdateStoryEvent.invoke(storyEvent)
	}

	override suspend fun updateStoryEvents(vararg storyEvents: StoryEvent) {
		this.storyEvents.putAll(storyEvents.associateBy { it.id })
		storyEvents.forEach(onUpdateStoryEvent)
	}
}