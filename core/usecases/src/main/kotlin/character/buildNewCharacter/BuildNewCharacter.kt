package com.soyle.stories.usecase.character.buildNewCharacter

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import java.util.*

interface BuildNewCharacter {

    suspend operator fun invoke(projectId: UUID, name: NonBlankString, outputPort: OutputPort)
    suspend fun createAndIncludeInTheme(name: NonBlankString, themeId: UUID, outputPort: OutputPort)
    suspend fun createAndUseAsOpponent(name: NonBlankString, themeId: UUID, opponentOfCharacterId: UUID, outputPort: OutputPort)

    interface OutputPort {
        fun receiveBuildNewCharacterFailure(failure: Exception)
        suspend fun receiveBuildNewCharacterResponse(response: CharacterItem)
        suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme)
        suspend fun characterIsOpponent(response: CharacterUsedAsOpponent)
    }
}