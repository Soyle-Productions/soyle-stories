package com.soyle.stories.usecase.theme.listSymbolsInTheme

import java.util.*

interface ListSymbolsInTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun symbolsListedInTheme(response: SymbolsInTheme)
    }

}