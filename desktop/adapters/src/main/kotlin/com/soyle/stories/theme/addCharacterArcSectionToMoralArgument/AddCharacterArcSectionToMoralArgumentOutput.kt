package com.soyle.stories.theme.addCharacterArcSectionToMoralArgument

import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.AddCharacterArcSectionToMoralArgument

class AddCharacterArcSectionToMoralArgumentOutput(
    private val arcSectionAddedToCharacterArcReceiver: ArcSectionAddedToCharacterArcReceiver
) : AddCharacterArcSectionToMoralArgument.OutputPort {

    override suspend fun characterArcSectionAddedToMoralArgument(response: AddCharacterArcSectionToMoralArgument.ResponseModel) {
        arcSectionAddedToCharacterArcReceiver.receiveArcSectionAddedToCharacterArc(
            response.characterArcSectionAddedToMoralArgument
        )
    }

}