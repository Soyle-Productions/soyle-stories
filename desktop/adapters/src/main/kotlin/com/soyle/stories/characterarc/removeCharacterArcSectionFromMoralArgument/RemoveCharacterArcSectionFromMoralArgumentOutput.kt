package com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgumentReceiver
import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgument

class RemoveCharacterArcSectionFromMoralArgumentOutput(
    private val characterArcSectionRemovedReceiver: CharacterArcSectionRemovedReceiver,
    private val characterArcSectionMovedInMoralArgumentReceiver: CharacterArcSectionMovedInMoralArgumentReceiver
) : RemoveCharacterArcSectionFromMoralArgument.OutputPort {

    override suspend fun removedCharacterArcSectionFromMoralArgument(
        response: RemoveCharacterArcSectionFromMoralArgument.ResponseModel
    ) {
        characterArcSectionRemovedReceiver.receiveCharacterArcSectionRemoved(response.removedSection)
        if (response.movedSections.isNotEmpty()) {
            characterArcSectionMovedInMoralArgumentReceiver
                .receiveCharacterArcSectionsMovedInMoralArgument(response.movedSections)
        }
    }

}