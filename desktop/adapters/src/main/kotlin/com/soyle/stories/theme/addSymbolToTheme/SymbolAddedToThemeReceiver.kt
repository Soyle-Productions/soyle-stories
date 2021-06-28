package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme

interface SymbolAddedToThemeReceiver {
    suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme)
}