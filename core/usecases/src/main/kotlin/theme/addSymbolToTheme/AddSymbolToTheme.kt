package com.soyle.stories.usecase.theme.addSymbolToTheme

import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface AddSymbolToTheme {

    suspend operator fun invoke(themeId: UUID, name: NonBlankString, output: OutputPort)

    interface OutputPort {
        suspend fun addedSymbolToTheme(response: SymbolAddedToTheme)
    }

}