package com.soyle.stories.usecase.theme.compareCharacterValues

import java.util.*

interface CompareCharacterValues {

    suspend operator fun invoke(themeId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun charactersCompared(response: CharacterValueComparison)
    }

}