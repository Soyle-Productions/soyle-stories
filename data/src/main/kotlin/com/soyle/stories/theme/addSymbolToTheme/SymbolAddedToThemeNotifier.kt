package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import kotlin.coroutines.coroutineContext

class SymbolAddedToThemeNotifier : Notifier<SymbolAddedToThemeReceiver>(), SymbolAddedToThemeReceiver {
    override suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme) {
        notifyAll(coroutineContext) { it.receiveSymbolAddedToTheme(symbolAddedToTheme) }
    }
}