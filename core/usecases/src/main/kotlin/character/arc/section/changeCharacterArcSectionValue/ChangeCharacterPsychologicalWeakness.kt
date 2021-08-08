package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import java.util.*

interface ChangeCharacterPsychologicalWeakness {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val psychologicalWeakness: String
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterArcSectionAddedToArc: ArcSectionAddedToCharacterArc?,
        val changedCharacterPsychologicalWeakness: ChangedCharacterArcSectionValue?
    )

    fun interface OutputPort {
        suspend fun characterPsychologicalWeaknessChanged(response: ResponseModel)
    }

}