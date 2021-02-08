package com.soyle.stories.theme.addCharacterArcSectionToMoralArgument

import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc

interface ArcSectionAddedToCharacterArcReceiver {

    suspend fun receiveArcSectionAddedToCharacterArc(event: ArcSectionAddedToCharacterArc)

}