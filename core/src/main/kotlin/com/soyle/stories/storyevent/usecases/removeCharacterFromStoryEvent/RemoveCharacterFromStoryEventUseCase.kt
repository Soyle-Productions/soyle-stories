package com.soyle.stories.storyevent.usecases.removeCharacterFromStoryEvent

import arrow.core.toT
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.CharacterNotInStoryEvent
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import java.util.*

class RemoveCharacterFromStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository
) : RemoveCharacterFromStoryEvent {


	override suspend fun invoke(storyEventId: UUID, characterId: UUID, output: RemoveCharacterFromStoryEvent.OutputPort) {
		val response = try {
			removeCharacterFromStoryEvent(storyEventId, characterId)
		} catch (s: StoryEventException){
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
			.map { it.withoutCharacterId(character) }
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
		storyEventRepository.updateStoryEvent(storyEvent.withoutCharacterId(Character.Id(characterId)))
		return RemoveCharacterFromStoryEvent.ResponseModel(storyEventId, characterId)
	}

	private fun validateCharacterId(storyEvent: StoryEvent, characterId: UUID, storyEventId: UUID) {
		if (!storyEvent.includedCharacterIds.contains(Character.Id(characterId))) {
			throw CharacterNotInStoryEvent(storyEventId, characterId)
		}
	}

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))
}