package com.soyle.stories.usecase.theme.useCharacterAsOpponent

import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import java.util.*

interface UseCharacterAsOpponent {

    class RequestModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val opponentId: UUID
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterAsOpponent: CharacterUsedAsOpponent,
        val includedCharacter: CharacterIncludedInTheme?
    )

    interface OutputPort {
        suspend fun characterIsOpponent(response: ResponseModel)
    }

}