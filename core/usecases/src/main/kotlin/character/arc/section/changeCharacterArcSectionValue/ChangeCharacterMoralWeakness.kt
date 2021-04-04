package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import java.util.*

interface ChangeCharacterMoralWeakness {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val moralWeakness: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val changedCharacterMoralWeakness: ChangedCharacterArcSectionValue
    )

    interface OutputPort {
        suspend fun characterMoralWeaknessChanged(response: ResponseModel)
    }

}