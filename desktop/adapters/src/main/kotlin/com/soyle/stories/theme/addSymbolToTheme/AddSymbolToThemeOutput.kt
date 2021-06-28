package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.usecase.theme.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme

class AddSymbolToThemeOutput(
    private val symbolAddedToThemeReceiver: SymbolAddedToThemeReceiver
) : AddSymbolToTheme.OutputPort {
    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        symbolAddedToThemeReceiver.receiveSymbolAddedToTheme(response)
    }
}