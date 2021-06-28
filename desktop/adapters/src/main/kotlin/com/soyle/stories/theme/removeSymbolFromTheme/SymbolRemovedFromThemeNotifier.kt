package com.soyle.stories.theme.removeSymbolFromTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.SymbolRemovedFromTheme

class SymbolRemovedFromThemeNotifier : Notifier<SymbolRemovedFromThemeReceiver>(), SymbolRemovedFromThemeReceiver {
    override suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme) {
        notifyAll { it.receiveSymbolRemovedFromTheme(symbolRemovedFromTheme) }
    }
}