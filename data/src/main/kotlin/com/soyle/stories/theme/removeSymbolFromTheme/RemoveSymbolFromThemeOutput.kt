package com.soyle.stories.theme.removeSymbolFromTheme

import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme

class RemoveSymbolFromThemeOutput(
    private val symbolRemovedFromThemeReceiver: SymbolRemovedFromThemeReceiver
) : RemoveSymbolFromTheme.OutputPort {
    override suspend fun removedSymbolFromTheme(response: SymbolRemovedFromTheme) {
        symbolRemovedFromThemeReceiver.receiveSymbolRemovedFromTheme(response)
    }
}