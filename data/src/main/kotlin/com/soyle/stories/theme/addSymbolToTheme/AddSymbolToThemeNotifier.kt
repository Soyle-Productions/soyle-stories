package com.soyle.stories.theme.addSymbolToTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme

class AddSymbolToThemeNotifier : Notifier<AddSymbolToTheme.OutputPort>(), AddSymbolToTheme.OutputPort {
    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        notifyAll { it.addedSymbolToTheme(response) }
    }
}