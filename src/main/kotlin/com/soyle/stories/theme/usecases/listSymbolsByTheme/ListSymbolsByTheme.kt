package com.soyle.stories.theme.usecases.listSymbolsByTheme

import java.util.*

interface ListSymbolsByTheme {

    suspend operator fun invoke(projectId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun symbolsListedByTheme(response: SymbolsByTheme)
    }

}