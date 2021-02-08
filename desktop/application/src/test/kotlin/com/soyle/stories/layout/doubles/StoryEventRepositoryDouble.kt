package com.soyle.stories.layout.doubles

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.usecases.StoryEventRepository

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

	override suspend fun getStoryEventsWithCharacter(characterId: Character.Id): List<StoryEvent> {
		return storyEvents.values.filter { it.includedCharacterIds.contains(characterId) }
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