package com.soyle.stories.character.usecases.buildNewCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.common.NonBlankString
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent
import java.util.*

interface BuildNewCharacter {

    suspend operator fun invoke(projectId: UUID, name: NonBlankString, outputPort: OutputPort)
    suspend fun createAndIncludeInTheme(name: NonBlankString, themeId: UUID, outputPort: OutputPort)
    suspend fun createAndUseAsOpponent(name: NonBlankString, themeId: UUID, opponentOfCharacterId: UUID, outputPort: OutputPort)

    interface OutputPort {
        fun receiveBuildNewCharacterFailure(failure: CharacterException)
        suspend fun receiveBuildNewCharacterResponse(response: CharacterItem)
        suspend fun characterIncludedInTheme(response: CharacterIncludedInTheme)
        suspend fun characterIsOpponent(response: CharacterUsedAsOpponent)
    }
}