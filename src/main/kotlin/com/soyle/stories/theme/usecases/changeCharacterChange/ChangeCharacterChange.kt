package com.soyle.stories.theme.usecases.changeCharacterChange

import java.util.*

interface ChangeCharacterChange {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val characterChange: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val changedCharacterChange: ChangedCharacterChange
    )

    interface OutputPort {
        suspend fun characterChangeChanged(response: ResponseModel)
    }

}