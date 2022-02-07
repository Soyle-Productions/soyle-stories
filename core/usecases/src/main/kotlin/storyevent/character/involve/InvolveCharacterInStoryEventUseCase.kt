package com.soyle.stories.usecase.storyevent.character.involve

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SuccessfulSceneUpdate
import com.soyle.stories.domain.scene.character.events.CharacterIncludedInScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.UnSuccessful
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.scene.SceneRepository
import com.soyle.stories.usecase.shared.exceptions.RejectedUpdateException
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

class InvolveCharacterInStoryEventUseCase(
	private val storyEventRepository: StoryEventRepository,
	private val characterRepository: CharacterRepository,
) : InvolveCharacterInStoryEvent {

	override suspend fun invoke(
		storyEventId: StoryEvent.Id,
		characterId: Character.Id,
		output: InvolveCharacterInStoryEvent.OutputPort
	) {
		val storyEvent = storyEventRepository.getStoryEventOrError(storyEventId)
		val character = characterRepository.getCharacterOrError(characterId.uuid)
		val storyEventUpdate = storyEvent.withCharacterInvolved(character)
		if (storyEventUpdate is Successful) {
			storyEventRepository.updateStoryEvent(storyEventUpdate.storyEvent)
				?.let { throw RejectedUpdateException("Story event could not be saved", it) }

			output.characterInvolvedInStoryEvent(storyEventUpdate.change)
		} else {
			storyEventUpdate as UnSuccessful
			storyEventUpdate.reason?.let { throw it }
		}
	}

}