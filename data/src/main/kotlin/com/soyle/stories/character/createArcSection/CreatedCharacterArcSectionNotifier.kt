package com.soyle.stories.character.createArcSection

import com.soyle.stories.common.Notifier
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ArcSectionAddedToCharacterArc

class CreatedCharacterArcSectionNotifier : Notifier<CreatedCharacterArcSectionReceiver>(), CreatedCharacterArcSectionReceiver {

    override suspend fun receiveCreatedCharacterArcSection(event: ArcSectionAddedToCharacterArc) {
        notifyAll { it.receiveCreatedCharacterArcSection(event) }
    }

}