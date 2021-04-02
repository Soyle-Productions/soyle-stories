package com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument

import java.util.*

interface AddCharacterArcSectionToMoralArgument {

    class RequestModel(
        val themeId: UUID,
        val characterId: UUID,
        val templateSectionId: UUID,
        val indexInMoralArgument: Int? = null
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val characterArcSectionAddedToMoralArgument: ArcSectionAddedToCharacterArc,
    )

    interface OutputPort {
        suspend fun characterArcSectionAddedToMoralArgument(response: ResponseModel)
    }

}