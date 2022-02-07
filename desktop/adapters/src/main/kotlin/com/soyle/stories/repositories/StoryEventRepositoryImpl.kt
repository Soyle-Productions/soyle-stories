package com.soyle.stories.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.SuccessfulStoryEventUpdate
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

    override suspend fun listStoryEventsInProject(projectId: Project.Id): List<StoryEvent> =
        storyEvents.values.filter { it.projectId == projectId }

    override suspend fun getStoryEventsWithCharacter(characterId: Character.Id): List<StoryEvent> {
        return storyEvents.values.filter { it.involvedCharacters.containsEntityWithId(characterId) }
    }

    override suspend fun getStoryEventsCoveredByScene(sceneId: Scene.Id): List<StoryEvent> {
        return storyEvents.values.filter { it.sceneId == sceneId }
    }

    override suspend fun getStoryEventsCoveredBySceneAndInvolvingCharacter(
        sceneId: Scene.Id,
        characterId: Character.Id
    ): List<StoryEvent> {
        return storyEvents.values.filter {
            it.sceneId == sceneId && it.involvedCharacters.containsEntityWithId(
                characterId
            )
        }
    }

    override suspend fun updateStoryEvent(storyEvent: StoryEvent): Throwable? {
        storyEvents[storyEvent.id] = storyEvent
        return null
    }

    override suspend fun save(update: SuccessfulStoryEventUpdate<*>): Throwable? {
        storyEvents[update.storyEvent.id] = update.storyEvent
        return null
    }

    override suspend fun updateStoryEvents(vararg storyEvents: StoryEvent) {
        this.storyEvents.putAll(storyEvents.associateBy { it.id })
    }

    override suspend fun removeStoryEvent(storyEventId: StoryEvent.Id) {
        this.storyEvents.remove(storyEventId)
    }

    override suspend fun getStoryEventWithCharacterNotNamed(characterId: Character.Id, name: String): StoryEvent? {
        TODO("Not yet implemented")
    }

    override suspend fun trySave(storyEvent: StoryEvent): Boolean {
        TODO("Not yet implemented")
    }

}