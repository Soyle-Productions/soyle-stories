package com.soyle.stories.character

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme

class TestContext(
  initialCharacters: List<Character> = emptyList(),
  initialThemes: List<Theme> = emptyList(),

  addNewCharacter: (Character) -> Unit = {},
  updateCharacter: (Character) -> Unit = {},
  deleteCharacterWithId: (Character.Id) -> Unit = {},

  updateThemes: (List<Theme>) -> Unit = {},
  deleteThemes: (List<Theme>) -> Unit = {}
) {

	data class PersistenceLog(val type: String, val data: Any) {
		override fun toString(): String {
			return "$type -> $data)"
		}
	}

	private val _persistedItems = mutableListOf<PersistenceLog>()
	val persistedItems: List<PersistenceLog>
		get() = _persistedItems

	val characterRepository: CharacterRepository = object : CharacterRepository {
		private val characters = initialCharacters.associateBy { it.id }.toMutableMap()
		override suspend fun addNewCharacter(character: Character) {
			_persistedItems.add(PersistenceLog("addNewCharacter", character))
			addNewCharacter.invoke(character)
			characters[character.id] = character
		}

		override suspend fun deleteCharacterWithId(characterId: Character.Id) {
			_persistedItems.add(PersistenceLog("deleteCharacterWithId", characterId))
			deleteCharacterWithId.invoke(characterId)
			characters.remove(characterId)
		}

		override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

		override suspend fun updateCharacter(character: Character) {
			_persistedItems.add(PersistenceLog("updateCharacter", character))
			updateCharacter.invoke(character)
			characters[character.id] = character
		}

		override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> {
			TODO("Not yet implemented")
		}
	}

	val themeRepository: ThemeRepository = object : ThemeRepository {
		private val themes = initialThemes.associateBy { it.id }.toMutableMap()
		override suspend fun deleteThemes(themes: List<Theme>) {
			_persistedItems.add(PersistenceLog("deleteThemes", themes))
			deleteThemes.invoke(themes)
			themes.forEach {
				this.themes.remove(it.id)
			}
		}

		override suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme> = this.themes.values.filter {
			it.containsCharacter(characterId)
		}

		override suspend fun updateThemes(themes: List<Theme>) {
			_persistedItems.add(PersistenceLog("updateThemes", themes))
			updateThemes.invoke(themes)
			themes.forEach {
				this.themes[it.id] = it
			}
		}

		override suspend fun getThemeById(id: Theme.Id): Theme? {
			return themes[id]
		}
	}

}