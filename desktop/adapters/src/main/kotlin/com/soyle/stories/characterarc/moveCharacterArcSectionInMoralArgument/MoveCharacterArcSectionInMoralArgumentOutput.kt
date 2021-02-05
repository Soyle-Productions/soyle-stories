package com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument

import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgument

class MoveCharacterArcSectionInMoralArgumentOutput(
    private val characterArcSectionMovedInMoralArgumentReceiver: CharacterArcSectionMovedInMoralArgumentReceiver
) : MoveCharacterArcSectionInMoralArgument.OutputPort {

    override suspend fun receiveMoveCharacterArcSectionInMoralArgumentResponse(response: MoveCharacterArcSectionInMoralArgument.ResponseModel) {
        characterArcSectionMovedInMoralArgumentReceiver.receiveCharacterArcSectionsMovedInMoralArgument(response)
    }
}