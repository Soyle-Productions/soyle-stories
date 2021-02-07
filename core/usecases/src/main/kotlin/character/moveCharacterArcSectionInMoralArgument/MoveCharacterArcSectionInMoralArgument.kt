package com.soyle.stories.usecase.character.moveCharacterArcSectionInMoralArgument

import java.util.*

interface MoveCharacterArcSectionInMoralArgument {

    class RequestModel(
        val themeId: UUID, val characterId: UUID, val arcSectionId: UUID, val index: Int
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort)

    class ResponseModel(
        val themeId: UUID,
        val characterId: UUID,
        val characterArcId: UUID,
        sections: List<CharacterArcSectionMovedInMoralArgument>
    ) : List<CharacterArcSectionMovedInMoralArgument> by sections

    interface OutputPort {
        suspend fun receiveMoveCharacterArcSectionInMoralArgumentResponse(response: ResponseModel)
    }

}