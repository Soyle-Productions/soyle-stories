package com.soyle.stories.usecase.character.changeCharacterArcSectionValue

import java.util.*

interface ChangeCharacterDesire {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val desire: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val changedCharacterDesire: ChangedCharacterArcSectionValue
    )

    interface OutputPort {
        suspend fun characterDesireChanged(response: ResponseModel)
    }

}