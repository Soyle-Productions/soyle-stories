package com.soyle.stories.usecase.character.changeCharacterArcSectionValue

import java.util.*

interface ChangeCharacterPsychologicalWeakness {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val psychologicalWeakness: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val changedCharacterPsychologicalWeakness: ChangedCharacterArcSectionValue
    )

    interface OutputPort {
        suspend fun characterPsychologicalWeaknessChanged(response: ResponseModel)
    }

}