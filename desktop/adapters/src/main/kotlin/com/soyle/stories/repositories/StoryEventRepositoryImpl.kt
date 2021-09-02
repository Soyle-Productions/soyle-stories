package com.soyle.stories.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventRepository

class StoryEventRepositoryImpl : StoryEventRepository {

	private val storyEvents = mutableMapOf<StoryEvent.Id, StoryEvent>()

	override suspend fun addNewStoryEvent(storyEvent: StoryEvent) {
		storyEvents[storyEvent.id] = storyEvent
	}

	override suspend fun getLastStoryEventInProject(projectId: Project.Id): StoryEvent? = storyEvents.values.find {
		it.nextStoryEventId == null && it.projectId == projectId
	}

	override suspend fun getStoryEventById(storyEventId: StoryEvent.Id): StoryEvent? = storyEvents[storyEventId]

	override suspend fun listStoryEventsInProject(projectId: Project.Id): List<StoryEvent> = storyEvents.values.filter { it.projectId == projectId }

	override suspend fun getStoryEventsWithCharacter(characterId: Character.Id): List<StoryEvent> {
		return storyEvents.values.filter { it.includedCharacterIds.contains(characterId) }
	}

	override suspend fun updateStoryEvent(storyEvent: StoryEvent) {
		storyEvents[storyEvent.id] = storyEvent
	}

	override suspend fun updateStoryEvents(vararg storyEvents: StoryEvent) {
		this.storyEvents.putAll(storyEvents.associateBy { it.id })
	}

	override suspend fun removeStoryEvent(storyEventId: StoryEvent.Id) {
		this.storyEvents.remove(storyEventId)
	}

}