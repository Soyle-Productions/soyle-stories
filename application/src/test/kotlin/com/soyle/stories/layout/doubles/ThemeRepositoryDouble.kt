package com.soyle.stories.layout.doubles

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

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

	override suspend fun addTheme(theme: Theme) {
		TODO("Not yet implemented")
	}

	override suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme? {
		TODO("Not yet implemented")
	}

	override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
		TODO("Not yet implemented")
	}

	override suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme? {
		TODO("Not yet implemented")
	}

	override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
		TODO("Not yet implemented")
	}

	override suspend fun getThemeContainingOppositionsWithSymbolicEntityId(symbolicId: UUID): List<Theme> {
		TODO("Not yet implemented")
	}

	override suspend fun updateThemes(themes: List<Theme>) {
		TODO("Not yet implemented")
	}
}