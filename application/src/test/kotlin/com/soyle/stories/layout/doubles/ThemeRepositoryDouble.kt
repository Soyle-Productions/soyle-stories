package com.soyle.stories.layout.doubles

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.ThemeRepository

class ThemeRepositoryDouble(
  initialThemes: List<Theme> = listOf(),

  private val onUpdateTheme: (Theme) -> Unit = {},
  private val onDeleteTheme: (Theme) -> Unit = {}
) : ThemeRepository {

	val themes = initialThemes.associateBy { it.id }.toMutableMap()

	override suspend fun getThemeById(id: Theme.Id): Theme? {
		return themes[id]
	}
	override suspend fun updateTheme(theme: Theme) {
		themes[theme.id] = theme
		onUpdateTheme(theme)
	}

	override suspend fun deleteTheme(theme: Theme) {
		themes.remove(theme.id)
		onDeleteTheme(theme)
	}
}