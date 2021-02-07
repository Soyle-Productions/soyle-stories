package com.soyle.stories.usecase.character.listCharactersAvailableToIncludeInTheme

import java.util.*

interface ListCharactersAvailableToIncludeInTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun availableCharactersToIncludeInThemeListed(response: CharactersAvailableToIncludeInTheme)
    }

}