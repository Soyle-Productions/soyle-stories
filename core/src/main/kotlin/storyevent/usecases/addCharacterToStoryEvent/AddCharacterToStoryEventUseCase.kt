package com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import java.util.*

class AddCharacterToStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository,
  private val characterRepository: CharacterRepository
) : AddCharacterToStoryEvent {

	override suspend fun invoke(storyEventId: UUID, characterId: UUID, output: AddCharacterToStoryEvent.OutputPort) {
		val response = try {
			addCharacterToStoryEvent(storyEventId, characterId)
		} catch (e: Exception) {
			return output.receiveAddCharacterToStoryEventFailure(e)
		}
		output.receiveAddCharacterToStoryEventResponse(response)
	}

	private suspend fun addCharacterToStoryEvent(storyEventId: UUID, characterId: UUID): AddCharacterToStoryEvent.ResponseModel {
		val storyEvent = getStoryEvent(storyEventId)
		val character = getCharacter(characterId)
		updateIfNeeded(storyEvent, character)
		return AddCharacterToStoryEvent.ResponseModel(storyEventId, characterId)
	}

	private suspend fun updateIfNeeded(storyEvent: StoryEvent, character: Character) {
		if (!storyEvent.includedCharacterIds.contains(character.id)) {
			storyEventRepository.updateStoryEvent(storyEvent.withIncludedCharacterId(character.id))
		}
	}

	private suspend fun getCharacter(characterId: UUID) =
	  (characterRepository.getCharacterById(Character.Id(characterId))
		?: throw CharacterDoesNotExist(characterId))

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))

}