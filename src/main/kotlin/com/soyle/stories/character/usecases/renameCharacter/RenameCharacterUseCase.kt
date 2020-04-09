package com.soyle.stories.character.usecases.renameCharacter

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.CharacterNameCannotBeBlank
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import java.util.*

class RenameCharacterUseCase(
  private val characterRepository: CharacterRepository
) : RenameCharacter {

	override suspend fun invoke(characterId: UUID, name: String, output: RenameCharacter.OutputPort) {
		val response = try {
			renameCharacter(characterId, name)
		} catch (c: CharacterException) {
			return output.receiveRenameCharacterFailure(c)
		}
		output.receiveRenameCharacterResponse(response)
	}

	private suspend fun renameCharacter(characterId: UUID, name: String): RenameCharacter.ResponseModel {
		val character = getCharacter(characterId)
		if (character.name == name) return RenameCharacter.ResponseModel(characterId, name)
		rename(character, name)
		return RenameCharacter.ResponseModel(characterId, name)
	}

	private suspend fun getCharacter(characterId: UUID): Character {
		return characterRepository.getCharacterById(Character.Id(characterId)) ?:
		  throw CharacterDoesNotExist(characterId)
	}

	private suspend fun rename(character: Character, newName: String) {
		character.rename(newName).fold(
		  { throw it },
		  { characterRepository.updateCharacter(it) }
		)
	}

}