package com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme

import java.util.*

interface ListCharactersAvailableToIncludeInTheme {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun availableCharactersToIncludeInThemeListed(response: CharactersAvailableToIncludeInTheme)
    }

}