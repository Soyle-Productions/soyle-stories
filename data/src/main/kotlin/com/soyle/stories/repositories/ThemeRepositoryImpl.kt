package com.soyle.stories.repositories

import com.soyle.stories.characterarc.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme

class ThemeRepositoryImpl : ThemeRepository, com.soyle.stories.theme.repositories.ThemeRepository, com.soyle.stories.character.repositories.ThemeRepository {
	val themes = mutableMapOf<Theme.Id, Theme>()
	override suspend fun addNewTheme(theme: Theme) {
		themes[theme.id] = theme
	}

	override suspend fun getThemeById(themeId: Theme.Id): Theme? = themes[themeId]
	override suspend fun updateTheme(theme: Theme) {
		themes[theme.id] = theme
	}

	override suspend fun deleteThemes(themes: List<Theme>) {
		themes.forEach {
			this.themes.remove(it.id)
		}
	}

	override suspend fun deleteTheme(theme: Theme) {
		deleteThemes(listOf(theme))
	}

	override suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme> {
		return themes.values.filter { it.containsCharacter(characterId) }.toList()
	}

	override suspend fun updateThemes(themes: List<Theme>) {
		this.themes.putAll(themes.map { it.id to it })
	}
}