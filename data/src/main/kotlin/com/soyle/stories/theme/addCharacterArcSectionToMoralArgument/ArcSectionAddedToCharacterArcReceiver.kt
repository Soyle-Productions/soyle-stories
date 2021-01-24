package com.soyle.stories.theme.addCharacterArcSectionToMoralArgument

import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc

interface ArcSectionAddedToCharacterArcReceiver {

    suspend fun receiveArcSectionAddedToCharacterArc(event: ArcSectionAddedToCharacterArc)

}