package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol

interface RenamedSymbolReceiver {
    suspend fun receiveRenamedSymbol(renamedSymbol: RenamedSymbol)
}