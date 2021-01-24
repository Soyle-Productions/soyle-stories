package com.soyle.stories.doubles

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import java.util.*

class CharacterRepositoryDouble(
  initialCharacters: List<Character> = emptyList(),

  private val onAddNewCharacter: (Character) -> Unit = {},
  private val onUpdateCharacter: (Character) -> Unit = {},
  private val onDeleteCharacterWithId: (Character.Id) -> Unit = {}
) : CharacterRepository, com.soyle.stories.characterarc.repositories.CharacterRepository, com.soyle.stories.theme.repositories.CharacterRepository {

	val characters = initialCharacters.associateBy { it.id }.toMutableMap()
	val characterArcs: MutableMap<Character.Id, MutableMap<Theme.Id, CharacterArc>> = WeakHashMap()

	fun givenCharacter(character: Character) {
		characters[character.id] = character
	}

	private val _updatedCharacters = mutableListOf<Character>()
	val updatedCharacters: List<Character>
		get() = _updatedCharacters

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
		_updatedCharacters += character
		characters[character.id] = character
	}

	override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> {
		return characters.values.filter { it.projectId == projectId }
	}

	override suspend fun getCharacterIdsThatDoNotExist(characterIdsToTest: Set<Character.Id>): Set<Character.Id> {
		return characterIdsToTest.filterNot { it in characters }.toSet()
	}
}