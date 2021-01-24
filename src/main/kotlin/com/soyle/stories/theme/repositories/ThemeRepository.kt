package com.soyle.stories.theme.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.oppositionValue.OppositionValue
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.entities.theme.valueWeb.ValueWeb
import java.util.*

/**
 * Created by Brendan
 * Date: 2/24/2020
 * Time: 5:25 PM
 */
interface ThemeRepository {
    suspend fun listThemesInProject(projectId: Project.Id): List<Theme>
    suspend fun getThemeById(id: Theme.Id): Theme?
    suspend fun getThemeContainingSymbolWithId(symbolId: Symbol.Id): Theme?
    suspend fun getThemeContainingValueWebWithId(valueWebId: ValueWeb.Id): Theme?
    suspend fun getThemeContainingOppositionValueWithId(oppositionValueId: OppositionValue.Id): Theme?
    suspend fun getThemeContainingOppositionsWithSymbolicEntityId(symbolicId: UUID): List<Theme>
    suspend fun addTheme(theme: Theme)
    suspend fun updateTheme(theme: Theme)
    suspend fun updateThemes(themes: List<Theme>)
    suspend fun deleteTheme(theme: Theme)
}