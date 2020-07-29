package com.soyle.stories.doubles

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.CharacterArcRepository
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
) : CharacterRepository, com.soyle.stories.characterarc.repositories.CharacterRepository, com.soyle.stories.theme.repositories.CharacterRepository, CharacterArcRepository {

	val characters = initialCharacters.associateBy { it.id }.toMutableMap()
	val characterArcs: MutableMap<Character.Id, MutableMap<Theme.Id, CharacterArc>> = WeakHashMap()

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

	override suspend fun addNewCharacterArc(characterArc: CharacterArc) {
		val arcMap = characterArcs.getOrPut(characterArc.characterId) { mutableMapOf() }
		arcMap[characterArc.themeId] = characterArc
	}

	override suspend fun getCharacterArcByCharacterAndThemeId(
		characterId: Character.Id,
		themeId: Theme.Id
	): CharacterArc? = characterArcs[characterId]?.get(themeId)

	override suspend fun listAllCharacterArcsInProject(projectId: Project.Id): List<CharacterArc> {
		return characters.values
			.asSequence()
			.filter { it.projectId == projectId }
			.mapNotNull { characterArcs[it.id] }
			.flatMap { it.values.asSequence() }.toList()
	}

	override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> {
		return characters.values.filter { it.projectId == projectId }
	}

	override suspend fun updateCharacterArc(characterArc: CharacterArc) {
		val arcMap = characterArcs.getOrPut(characterArc.characterId) { mutableMapOf() }
		arcMap[characterArc.themeId] = characterArc
	}
}