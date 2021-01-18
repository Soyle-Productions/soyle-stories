package com.soyle.stories.character

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.ThemeRepository
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.entities.Character
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

	val characterRepository: CharacterRepository = CharacterRepositoryDouble(
		initialCharacters = initialCharacters,
		onAddNewCharacter = {
			_persistedItems.add(PersistenceLog("addNewCharacter", it))
			addNewCharacter.invoke(it)
		},
		onUpdateCharacter = {
			_persistedItems.add(PersistenceLog("updateCharacter", it))
			updateCharacter.invoke(it)
		},
		onDeleteCharacterWithId = {
			_persistedItems.add(PersistenceLog("deleteCharacterWithId", it))
			deleteCharacterWithId.invoke(it)
		}
	)

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