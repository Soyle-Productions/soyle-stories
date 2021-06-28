package com.soyle.stories.character.createArcSection

import com.soyle.stories.usecase.character.arc.section.addSectionToArc.AddSectionToCharacterArc

class AddSectionToCharacterArcOutput(
    private val createdCharacterArcSectionReceiver: CreatedCharacterArcSectionReceiver
) : AddSectionToCharacterArc.OutputPort {
    override suspend fun receiveAddSectionToCharacterArcResponse(response: AddSectionToCharacterArc.ResponseModel) {
        createdCharacterArcSectionReceiver.receiveCreatedCharacterArcSection(
            response.sectionAddedToCharacterArc
        )
    }
}