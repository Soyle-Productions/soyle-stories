package com.soyle.stories.characterarc

import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme

class TestContext(
  initialCharacters: List<Character> = emptyList(),
  initialThemes: List<Theme> = emptyList(),
  onUpdateTheme: (Theme) -> Unit = {}
) {

	val characterRepository = object : CharacterRepository {
		val characters = initialCharacters.associateBy { it.id }.toMutableMap()
		override suspend fun getCharacterById(characterId: Character.Id): Character? = characters[characterId]

		override suspend fun listCharactersInProject(projectId: Project.Id): List<Character> = characters.values.filter {
			it.projectId == projectId
		}
	}

	val themeRepository: ThemeRepository = object : ThemeRepository {
		val themes = initialThemes.associateBy { it.id }.toMutableMap()
		override suspend fun addNewTheme(theme: Theme) {
			themes[theme.id] = theme
		}

		override suspend fun getThemeById(themeId: Theme.Id): Theme? = themes[themeId]

		override suspend fun listAllThemesInProject(projectId: Project.Id): List<Theme> {
			return themes.values.filter { it.projectId == projectId }
		}

		override suspend fun updateTheme(theme: Theme) {
			onUpdateTheme(theme)
			themes[theme.id] = theme
		}
	}

}