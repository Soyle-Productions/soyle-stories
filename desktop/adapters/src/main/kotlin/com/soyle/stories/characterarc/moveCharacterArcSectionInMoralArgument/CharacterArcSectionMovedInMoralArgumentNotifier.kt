package com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument

import com.soyle.stories.usecase.character.arc.section.moveCharacterArcSectionInMoralArgument.CharacterArcSectionMovedInMoralArgument
import com.soyle.stories.common.Notifier

class CharacterArcSectionMovedInMoralArgumentNotifier : CharacterArcSectionMovedInMoralArgumentReceiver, Notifier<CharacterArcSectionMovedInMoralArgumentReceiver>() {

    override suspend fun receiveCharacterArcSectionsMovedInMoralArgument(events: List<CharacterArcSectionMovedInMoralArgument>) {
        notifyAll { it.receiveCharacterArcSectionsMovedInMoralArgument(events) }
    }
}