package com.soyle.stories.theme.usecases.changeCharacterDesire

import java.util.*

interface ChangeCharacterDesire {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val desire: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val changedCharacterDesire: ChangedCharacterDesire
    )

    interface OutputPort {
        suspend fun characterDesireChanged(response: ResponseModel)
    }

}