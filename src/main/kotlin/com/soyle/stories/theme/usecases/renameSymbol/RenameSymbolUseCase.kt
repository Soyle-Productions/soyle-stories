package com.soyle.stories.theme.usecases.renameSymbol

import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.SymbolAlreadyHasName
import com.soyle.stories.theme.SymbolDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.validateSymbolName
import java.util.*

class RenameSymbolUseCase(
    private val themeRepository: ThemeRepository
) : RenameSymbol {

    override suspend fun invoke(symbolId: UUID, name: String, output: RenameSymbol.OutputPort) {
        validateSymbolName(name)
        val theme = getThemeContainingSymbol(symbolId)
        val symbol = theme.symbols.find { it.id.uuid == symbolId }!!
        if (symbol.name == name) throw SymbolAlreadyHasName(symbolId, name)
        themeRepository.updateTheme(theme.withoutSymbol(symbol.id).withSymbol(symbol.withName(name)))
        output.symbolRenamed(RenamedSymbol(theme.id.uuid, symbolId, name))
    }

    private suspend fun getThemeContainingSymbol(symbolId: UUID) =
        (themeRepository.getThemeContainingSymbolWithId(Symbol.Id(symbolId))
            ?: throw SymbolDoesNotExist(symbolId))

}