package com.soyle.stories.theme.usecases.addSymbolToTheme

import java.util.*

interface AddSymbolToTheme {

    suspend operator fun invoke(themeId: UUID, name: String, output: OutputPort)

    interface OutputPort {
        suspend fun addedSymbolToTheme(response: SymbolAddedToTheme)
    }

}