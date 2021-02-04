package com.soyle.stories.theme.usecases.listSymbolsInTheme

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.SymbolItem
import java.util.*

class ListSymbolsInThemeUseCase(
    private val themeRepository: ThemeRepository
) : ListSymbolsInTheme {

    override suspend fun invoke(themeId: UUID, output: ListSymbolsInTheme.OutputPort) {
        val theme = getTheme(themeId)

        output.symbolsListedInTheme(themeToResponseModel(theme))
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

    private fun themeToResponseModel(
        theme: Theme
    ) = SymbolsInTheme(theme.id.uuid, theme.name, theme.symbols.map(::symbolToItem))

    private fun symbolToItem(it: Symbol) = SymbolItem(it.id.uuid, it.name)
}