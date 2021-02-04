package com.soyle.stories.theme.usecases.addSymbolToTheme

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.SymbolNameCannotBeBlank
import com.soyle.stories.theme.usecases.validateSymbolName
import java.util.*

class AddSymbolToThemeUseCase(
    private val themeRepository: ThemeRepository
) : AddSymbolToTheme {

    override suspend fun invoke(themeId: UUID, name: String, output: AddSymbolToTheme.OutputPort) {
        val theme = getTheme(themeId)
        validateSymbolName(name)
        val symbol = createSymbol(name, theme)
        output.addedSymbolToTheme(SymbolAddedToTheme(themeId, symbol.id.uuid, name))
    }

    private suspend fun createSymbol(name: String, theme: Theme): Symbol {
        val symbol = Symbol(name)
        themeRepository.updateTheme(theme.withSymbol(symbol))
        return symbol
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))
}