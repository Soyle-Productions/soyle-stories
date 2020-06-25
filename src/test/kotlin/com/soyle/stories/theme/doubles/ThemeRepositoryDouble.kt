package com.soyle.stories.theme.doubles

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.ValueWeb
import com.soyle.stories.theme.repositories.ThemeRepository

class ThemeRepositoryDouble(
    private val onAddTheme: (Theme) -> Unit = {},
    private val onUpdateTheme: (Theme) -> Unit = {},
    private val onDeleteTheme: (Theme) -> Unit = {}
) : ThemeRepository
{
    val themes = mutableMapOf<Theme.Id, Theme>()

    override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
        return themes.values.filter { it.projectId == projectId }
    }

    override suspend fun getThemeById(id: Theme.Id): Theme? = themes[id]

    override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
        return themes.values.find { it.symbols.any { it.id == symbolId } }
    }

    override suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme? {
        return themes.values.find { it.valueWebs.any { it.id == valueWebId } }
    }

    override suspend fun addTheme(theme: Theme) {
        themes[theme.id] = theme
        onAddTheme(theme)
    }

    override suspend fun updateTheme(theme: Theme) {
        themes[theme.id]= theme
        onUpdateTheme(theme)
    }

    override suspend fun deleteTheme(theme: Theme) {
        themes.remove(theme.id)
        onDeleteTheme(theme)
    }
}