package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project

/**
 * Read-only repository to retrieve story events
 */
interface StoryEventRepository {
    suspend fun getStoryEventWithCharacterNotNamed(characterId: Character.Id, name: String): StoryEvent?
    /** attempts to save the story event and returns true if successful.  False if the wrong version was encountered */
    suspend fun trySave(storyEvent: StoryEvent): Boolean
    suspend fun listStoryEventsInProject(projectId: Project.Id): List<StoryEvent>
}