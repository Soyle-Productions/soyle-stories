package com.soyle.stories.character.createArcSection

import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc

interface CreatedCharacterArcSectionReceiver {

    suspend fun receiveCreatedCharacterArcSection(event: ArcSectionAddedToCharacterArc)

}