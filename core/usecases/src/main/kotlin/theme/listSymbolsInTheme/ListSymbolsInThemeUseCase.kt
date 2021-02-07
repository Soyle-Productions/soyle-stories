package com.soyle.stories.usecase.theme.listSymbolsInTheme

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.theme.SymbolItem
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
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