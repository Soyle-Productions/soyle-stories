package com.soyle.stories.theme.removeSymbolFromTheme

import com.soyle.stories.usecase.theme.removeSymbolFromTheme.SymbolRemovedFromTheme

interface SymbolRemovedFromThemeReceiver {

    suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme)

}