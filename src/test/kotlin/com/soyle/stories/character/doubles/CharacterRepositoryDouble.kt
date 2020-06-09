package com.soyle.stories.character.doubles

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character

class CharacterRepositoryDouble(
  initialCharacters: List<Character> = emptyList(),

  private val onAddNewCharacter: (Character) -> Unit = {},
  private val onUpdateCharacter: (Character) -> Unit = {},
  private val onDeleteCharacterWithId: (Character.Id) -> Unit = {}
) : CharacterRepository {

	val characters = initialCharacters.associateBy { it.id }.toMutableMap()
	override suspend fun addNewCharacter(character: Character) {
		onAddNewCharacter.invoke(character)
		characters[character.id] = character
	}

	override suspend fun deleteCharacterWithId(characterId: Character.Id) {
		onDeleteCharacterWithId.invoke(characterId)
		characters.remove(characterId)
	}

	override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

	override suspend fun updateCharacter(character: Character) {
		onUpdateCharacter.invoke(character)
		characters[character.id] = character
	}
}