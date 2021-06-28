package com.soyle.stories.usecase.theme.listAvailableOppositionValuesForCharacterInTheme

import java.util.*

interface ListAvailableOppositionValuesForCharacterInTheme {

    suspend operator fun invoke(themeId: UUID, characterId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun availableOppositionValuesListedForCharacterInTheme(response: OppositionValuesAvailableForCharacterInTheme)
    }

}