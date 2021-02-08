package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme

class SymbolAddedToThemeNotifier : Notifier<SymbolAddedToThemeReceiver>(), SymbolAddedToThemeReceiver {
    override suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme) {
        notifyAll { it.receiveSymbolAddedToTheme(symbolAddedToTheme) }
    }
}