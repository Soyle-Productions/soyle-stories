package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme

interface SymbolAddedToThemeReceiver {
    suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme)
}