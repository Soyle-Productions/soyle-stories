package com.soyle.stories.theme.usecases.useCharacterAsOpponent

import java.util.*

interface UseCharacterAsOpponent {

    class RequestModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val opponentId: UUID
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    interface OutputPort {
        suspend fun characterIsOpponent(response: OpponentCharacter)
    }

}