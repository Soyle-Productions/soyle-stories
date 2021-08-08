package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import java.util.*

interface ChangeCharacterMoralWeakness {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val moralWeakness: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterArcSectionAddedToArc: ArcSectionAddedToCharacterArc?,
        val changedCharacterMoralWeakness: ChangedCharacterArcSectionValue?
    )

    fun interface OutputPort {
        suspend fun characterMoralWeaknessChanged(response: ResponseModel)
    }

}