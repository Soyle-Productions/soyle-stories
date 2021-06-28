package com.soyle.stories.usecase.theme.addSymbolToTheme

import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class AddSymbolToThemeUseCase(
    private val themeRepository: ThemeRepository
) : AddSymbolToTheme {

    override suspend fun invoke(themeId: UUID, name: NonBlankString, output: AddSymbolToTheme.OutputPort) {
        val theme = getTheme(themeId)
        val symbol = createSymbol(name, theme)
        output.addedSymbolToTheme(SymbolAddedToTheme(themeId, symbol.id.uuid, name.value))
    }

    private suspend fun createSymbol(name: NonBlankString, theme: Theme): Symbol {
        val symbol = Symbol(name.value)
        themeRepository.updateTheme(theme.withSymbol(symbol))
        return symbol
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))
}