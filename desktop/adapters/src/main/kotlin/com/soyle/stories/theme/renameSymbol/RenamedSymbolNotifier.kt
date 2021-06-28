package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.theme.renameSymbol.RenamedSymbol

class RenamedSymbolNotifier : Notifier<RenamedSymbolReceiver>(), RenamedSymbolReceiver {
    override suspend fun receiveRenamedSymbol(renamedSymbol: RenamedSymbol) {
        notifyAll { it.receiveRenamedSymbol(renamedSymbol) }
    }
}