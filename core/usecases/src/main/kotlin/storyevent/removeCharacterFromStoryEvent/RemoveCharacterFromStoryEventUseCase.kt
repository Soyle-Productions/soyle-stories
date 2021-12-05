package com.soyle.stories.usecase.storyevent.removeCharacterFromStoryEvent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.storyevent.CharacterNotInStoryEvent
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import java.util.*

class RemoveCharacterFromStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository
) : RemoveCharacterFromStoryEvent {


	override suspend fun invoke(storyEventId: UUID, characterId: UUID, output: RemoveCharacterFromStoryEvent.OutputPort) {
		val response = try {
			removeCharacterFromStoryEvent(storyEventId, characterId)
		} catch (s: Exception){
			return output.receiveRemoveCharacterFromStoryEventFailure(s)
		}
		output.receiveRemoveCharacterFromStoryEventResponse(response)
	}

	override suspend fun removeCharacterFromAllStoryEvents(
		characterId: UUID,
		output: RemoveCharacterFromStoryEvent.OutputPort
	) {
		val character = Character.Id(characterId)
		val updatedEvents = storyEventRepository.getStoryEventsWithCharacter(character)
			.map { it.withCharacterRemoved(character).storyEvent }
		if (updatedEvents.isNotEmpty()) {
			storyEventRepository.updateStoryEvents(*updatedEvents.toTypedArray())
			updatedEvents.forEach {
				output.receiveRemoveCharacterFromStoryEventResponse(
					RemoveCharacterFromStoryEvent.ResponseModel(it.id.uuid, characterId)
				)
			}
		}
	}

	private suspend fun removeCharacterFromStoryEvent(storyEventId: UUID, characterId: UUID): RemoveCharacterFromStoryEvent.ResponseModel {
		val storyEvent = getStoryEvent(storyEventId)
		validateCharacterId(storyEvent, characterId, storyEventId)
		storyEventRepository.updateStoryEvent(storyEvent.withCharacterRemoved(Character.Id(characterId)).storyEvent)
		return RemoveCharacterFromStoryEvent.ResponseModel(storyEventId, characterId)
	}

	private fun validateCharacterId(storyEvent: StoryEvent, characterId: UUID, storyEventId: UUID) {
		if (!storyEvent.involvedCharacters.contains(Character.Id(characterId))) {
			throw CharacterNotInStoryEvent(storyEventId, characterId)
		}
	}

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))
}