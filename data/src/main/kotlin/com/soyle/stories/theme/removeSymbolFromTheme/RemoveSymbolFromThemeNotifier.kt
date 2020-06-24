package com.soyle.stories.theme.removeSymbolFromTheme

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import kotlin.coroutines.coroutineContext

class RemoveSymbolFromThemeNotifier : Notifier<RemoveSymbolFromTheme.OutputPort>(), RemoveSymbolFromTheme.OutputPort {
    override suspend fun removedSymbolFromTheme(response: SymbolRemovedFromTheme) {
        notifyAll(coroutineContext) { it.removedSymbolFromTheme(response) }
    }
}