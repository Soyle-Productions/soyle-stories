package com.soyle.stories.theme.renameSymbol

import com.soyle.stories.usecase.theme.renameSymbol.RenamedSymbol

interface RenamedSymbolReceiver {
    suspend fun receiveRenamedSymbol(renamedSymbol: RenamedSymbol)
}