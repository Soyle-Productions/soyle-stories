package com.soyle.stories.usecase.storyevent.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SuccessfulSceneUpdate
import com.soyle.stories.domain.scene.character.events.CharacterRemovedFromScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.UnSuccessful
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.shared.exceptions.RejectedUpdateException
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class RemoveCharacterFromStoryEventUseCase(
    private val storyEventRepository: StoryEventRepository,
	private val sceneRepository: SceneRepository
) : RemoveCharacterFromStoryEvent {

    override suspend fun invoke(
        storyEventId: StoryEvent.Id,
        characterId: Character.Id,
        output: RemoveCharacterFromStoryEvent.OutputPort
    ) {
        val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
        when (val storyEventUpdate = storyEvent.withCharacterRemoved(characterId)) {
            is Successful -> {
                storyEventRepository.updateStoryEvent(storyEventUpdate.storyEvent)
                    ?.let { throw RejectedUpdateException("Could not update story event", it) }

                output.characterRemovedFromStoryEvent(storyEventUpdate.change)
            }
            is UnSuccessful -> {
                storyEventUpdate.reason?.let { throw it }
            }
        }
    }
}