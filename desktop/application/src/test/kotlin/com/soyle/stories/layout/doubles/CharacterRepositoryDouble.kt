package com.soyle.stories.layout.doubles

import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project

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

	override suspend fun getCharacterIdsThatDoNotExist(characterIdsToTest: Set<Character.Id>): Set<Character.Id> {
		return characterIdsToTest.filterNot { it in characters }.toSet()
	}

	override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

	override suspend fun updateCharacter(character: Character) {
		onUpdateCharacter.invoke(character)
		characters[character.id] = character
	}

	override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> {
		return characters.values.filter { it.projectId == projectId }
	}
}