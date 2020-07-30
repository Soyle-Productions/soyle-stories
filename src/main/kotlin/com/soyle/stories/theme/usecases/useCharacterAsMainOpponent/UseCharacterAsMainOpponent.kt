package com.soyle.stories.theme.usecases.useCharacterAsMainOpponent

import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
import java.util.*

interface UseCharacterAsMainOpponent {

    class RequestModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val opponentCharacterId: UUID
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val mainOpponent: OpponentCharacter,
        val previousMainOpponent: OpponentCharacter?
    )

    interface OutputPort {
        suspend fun characterUsedAsMainOpponent(response: ResponseModel)
    }

}