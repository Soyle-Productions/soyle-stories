package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ThemeRepositoryDouble(
    private val onAddTheme: (Theme) -> Unit = {},
    private val onUpdateTheme: (Theme) -> Unit = {},
    private val onDeleteTheme: (Theme) -> Unit = {}
) : ThemeRepository {

    val themes = mutableMapOf<Theme.Id, Theme>()

    fun givenTheme(theme: Theme) {
        themes[theme.id] = theme
    }

    override suspend fun listThemesInProject(projectId: Project.Id): List<Theme> {
        return themes.values.filter { it.projectId == projectId }
    }

    override suspend fun getThemeById(id: Theme.Id): Theme? = themes[id]

    override suspend fun getThemesById(themeIds: Set<Theme.Id>): Set<Theme> {
        return themeIds.mapNotNull(themes::get).toSet()
    }

    override suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme? {
        return themes.values.find { it.symbols.any { it.id == symbolId } }
    }

    override suspend fun getThemesContainingSymbols(symbolIds: Set<Symbol.Id>): Map<Symbol.Id, Theme> {
        return themes.values.asSequence()
            .flatMap { theme -> theme.symbols.asSequence().map { it.id to theme } }
            .filter { it.first in symbolIds }
            .toMap()
    }

    override suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme? {
        return themes.values.find { it.valueWebs.any { it.id == valueWebId } }
    }

    override suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme? {
        return themes.values.find {
            it.valueWebs.any {
                it.oppositions.any { it.id == oppositionValueId }
            }
        }
    }

    override suspend fun getThemeContainingOppositionsWithSymbolicEntityId(symbolicId: UUID): List<Theme> {
        return themes.values.filter {
            it.valueWebs.any {
                it.oppositions.any {
                    it.representations.any { it.entityUUID == symbolicId }
                }
            }
        }
    }

    override suspend fun addTheme(theme: Theme) {
        themes[theme.id] = theme
        onAddTheme(theme)
    }

    override suspend fun updateTheme(theme: Theme) {
        themes[theme.id]= theme
        onUpdateTheme(theme)
    }

    override suspend fun updateThemes(themes: List<Theme>) {
        themes.forEach { updateTheme(it) }
    }

    override suspend fun deleteTheme(theme: Theme) {
        themes.remove(theme.id)
        onDeleteTheme(theme)
    }

    override suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme> =
        themes.values.filter { it.containsCharacter(characterId) }

    override suspend fun getSymbolIdsThatDoNotExist(symbolIds: Set<Symbol.Id>): Set<Symbol.Id> {
        return symbolIds - themes.values.asSequence().flatMap { it.symbols.asSequence() }.map { it.id }.toSet()
    }
}