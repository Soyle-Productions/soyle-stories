package com.soyle.stories.theme.addCharacterArcSectionToMoralArgument

import com.soyle.stories.usecase.character.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc
import com.soyle.stories.common.Notifier

class ArcSectionAddedToCharacterArcNotifier : ArcSectionAddedToCharacterArcReceiver, Notifier<ArcSectionAddedToCharacterArcReceiver>() {


    override suspend fun receiveArcSectionAddedToCharacterArc(event: ArcSectionAddedToCharacterArc) {
        notifyAll { it.receiveArcSectionAddedToCharacterArc(event) }
    }
}