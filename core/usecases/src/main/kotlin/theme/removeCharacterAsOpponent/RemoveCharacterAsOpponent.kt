package com.soyle.stories.usecase.theme.removeCharacterAsOpponent

import java.util.*

interface RemoveCharacterAsOpponent {

    class RequestModel(
        val themeId: UUID,
        val perspectiveCharacterId: UUID,
        val opponentId: UUID
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterRemovedAsOpponent: CharacterRemovedAsOpponent
    )

    interface OutputPort {
        suspend fun removedCharacterAsOpponent(response: ResponseModel)
    }

}