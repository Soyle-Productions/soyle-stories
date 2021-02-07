package com.soyle.stories.usecase.character.changeCharacterArcSectionValue

import java.util.*

interface ChangeCharacterArcSectionValue {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val arcSectionId: UUID,
        val newValue: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val changedCharacterArcSectionValue: ChangedCharacterArcSectionValue
    )

    interface OutputPort {
        suspend fun characterArcSectionValueChanged(response: ResponseModel)
    }

}