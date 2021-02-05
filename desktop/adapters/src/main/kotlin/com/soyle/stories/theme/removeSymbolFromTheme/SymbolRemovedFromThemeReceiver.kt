package com.soyle.stories.theme.removeSymbolFromTheme

import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme

interface SymbolRemovedFromThemeReceiver {

    suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme)

}