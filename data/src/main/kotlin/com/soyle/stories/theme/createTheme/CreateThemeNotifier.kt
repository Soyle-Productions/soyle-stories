package com.soyle.stories.theme.createTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeNotifier
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import kotlin.coroutines.coroutineContext

class CreateThemeNotifier(private val addSymbolToThemeNotifier: AddSymbolToThemeNotifier) : Notifier<CreateTheme.OutputPort>(), CreateTheme.OutputPort {
    override suspend fun themeCreated(response: CreatedTheme) {
        notifyAll(coroutineContext) {
            it.themeCreated(response)
        }
    }

    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        addSymbolToThemeNotifier.addedSymbolToTheme(response)
    }
}