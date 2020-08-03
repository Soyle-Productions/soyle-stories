package com.soyle.stories.theme.usecases.useCharacterAsMainOpponent

import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent
import java.util.*

interface UseCharacterAsMainOpponent {

    class RequestModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val opponentCharacterId: UUID
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val mainOpponent: CharacterUsedAsMainOpponent,
        val previousMainOpponent: CharacterUsedAsOpponent?
    )

    interface OutputPort {
        suspend fun characterUsedAsMainOpponent(response: ResponseModel)
    }

}