package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.oppositionValue.OppositionValue
import com.soyle.stories.domain.theme.valueWeb.ValueWeb
import java.util.*

interface ThemeRepository {
    suspend fun listThemesInProject(projectId: Project.Id): List<Theme>
    suspend fun getThemesWithCharacterIncluded(characterId: Character.Id): List<Theme>
    suspend fun getThemeById(id: Theme.Id): Theme?
    suspend fun getThemeOrError(themeId: Theme.Id) = getThemeById(themeId)
        ?: throw ThemeDoesNotExist(themeId.uuid)
    suspend fun getThemesById(themeIds: Set<Theme.Id>): Set<Theme>
    suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme?
    suspend fun getThemesContainingSymbols(symbolIds: Set<Symbol.Id>): Map<Symbol.Id, Theme>
    suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme?
    suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme?
    suspend fun getThemeContainingOppositionsWithSymbolicEntityId(symbolicId: UUID): List<Theme>
    suspend fun addTheme(theme: Theme)
    suspend fun updateTheme(theme: Theme)
    suspend fun updateThemes(themes: List<Theme>)
    suspend fun deleteTheme(theme: Theme)
    suspend fun getSymbolIdsThatDoNotExist(symbolIds: Set<Symbol.Id>): Set<Symbol.Id>
}