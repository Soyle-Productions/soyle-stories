package com.soyle.stories.theme.addCharacterArcSectionToMoralArgument

import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgument

class AddCharacterArcSectionToMoralArgumentOutput(
    private val arcSectionAddedToCharacterArcReceiver: ArcSectionAddedToCharacterArcReceiver
) : AddCharacterArcSectionToMoralArgument.OutputPort {

    override suspend fun characterArcSectionAddedToMoralArgument(response: AddCharacterArcSectionToMoralArgument.ResponseModel) {
        arcSectionAddedToCharacterArcReceiver.receiveArcSectionAddedToCharacterArc(
            response.characterArcSectionAddedToMoralArgument
        )
    }

}