package com.soyle.stories.storyevent.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.StoryEvent

interface StoryEventRepository {

	suspend fun addNewStoryEvent(storyEvent: StoryEvent)
	suspend fun getStoryEventById(storyEventId: StoryEvent.Id): StoryEvent?
	suspend fun listStoryEventsInProject(projectId: Project.Id): List<StoryEvent>
	suspend fun getLastStoryEventInProject(projectId: Project.Id): StoryEvent?
	suspend fun updateStoryEvent(storyEvent: StoryEvent)
	suspend fun updateStoryEvents(vararg storyEvents: StoryEvent)

}