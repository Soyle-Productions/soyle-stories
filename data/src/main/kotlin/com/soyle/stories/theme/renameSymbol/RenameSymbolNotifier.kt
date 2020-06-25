package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import kotlin.coroutines.coroutineContext

class RenameSymbolNotifier : Notifier<RenameSymbol.OutputPort>(), RenameSymbol.OutputPort {
    override suspend fun symbolRenamed(response: RenamedSymbol) {
        notifyAll(coroutineContext) { it.symbolRenamed(response) }
    }
}