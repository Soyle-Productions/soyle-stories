package com.soyle.stories.character.createArcSection

import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc

interface CreatedCharacterArcSectionReceiver {

    suspend fun receiveCreatedCharacterArcSection(event: ArcSectionAddedToCharacterArc)

}