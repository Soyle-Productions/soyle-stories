package com.soyle.stories.theme.addCharacterArcSectionToMoralArgument

import com.soyle.stories.usecase.character.arc.section.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc

interface ArcSectionAddedToCharacterArcReceiver {

    suspend fun receiveArcSectionAddedToCharacterArc(event: ArcSectionAddedToCharacterArc)

}