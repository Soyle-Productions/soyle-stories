package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

interface StoryEventRepository : com.soyle.stories.domain.storyevent.StoryEventRepository {

    suspend fun addNewStoryEvent(storyEvent: StoryEvent)
    suspend fun getStoryEventById(storyEventId: StoryEvent.Id): StoryEvent?

    /**
     * @throws StoryEventDoesNotExist if the supplied story event does not exist in the project
     */
    suspend fun getStoryEventOrError(storyEventId: StoryEvent.Id): StoryEvent =
        getStoryEventById(storyEventId) ?: throw StoryEventDoesNotExist(storyEventId.uuid)

    suspend fun getStoryEventsWithCharacter(characterId: Character.Id): List<StoryEvent>
    suspend fun getLastStoryEventInProject(projectId: Project.Id): StoryEvent?
    suspend fun getStoryEventsCoveredByScene(sceneId: Scene.Id): List<StoryEvent>
    suspend fun updateStoryEvent(storyEvent: StoryEvent)
    suspend fun updateStoryEvents(vararg storyEvents: StoryEvent)

    suspend fun removeStoryEvent(storyEventId: StoryEvent.Id)

}