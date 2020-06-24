package com.soyle.stories.theme.usecases.removeSymbolFromTheme

import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class RemoveSymbolFromThemeUseCase(
    private val themeRepository: ThemeRepository
) : RemoveSymbolFromTheme {

    override suspend fun invoke(symbolId: UUID, output: RemoveSymbolFromTheme.OutputPort) {
        val theme = getThemeWithSymbol(symbolId)

        val symbol = theme.symbols.find { it.id.uuid == symbolId }!!

        themeRepository.updateTheme(theme.withoutSymbol(symbol.id))

        output.removedSymbolFromTheme(SymbolRemovedFromTheme(theme.id.uuid, symbol.id.uuid, symbol.name))
    }

    private suspend fun getThemeWithSymbol(symbolId: UUID) =
        (themeRepository.getThemeContainingSymbolWithId(Symbol.Id(symbolId))
            ?: throw SymbolDoesNotExist(symbolId))

}