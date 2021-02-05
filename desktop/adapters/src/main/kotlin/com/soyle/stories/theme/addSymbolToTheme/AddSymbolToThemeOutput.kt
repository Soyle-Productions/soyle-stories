package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme

class AddSymbolToThemeOutput(
    private val symbolAddedToThemeReceiver: SymbolAddedToThemeReceiver
) : AddSymbolToTheme.OutputPort {
    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        symbolAddedToThemeReceiver.receiveSymbolAddedToTheme(response)
    }
}