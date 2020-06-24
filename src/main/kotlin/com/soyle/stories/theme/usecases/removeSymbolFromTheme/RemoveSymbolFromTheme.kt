package com.soyle.stories.theme.usecases.removeSymbolFromTheme

import java.util.*

interface RemoveSymbolFromTheme {

    suspend operator fun invoke(symbolId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun removedSymbolFromTheme(response: SymbolRemovedFromTheme)
    }

}