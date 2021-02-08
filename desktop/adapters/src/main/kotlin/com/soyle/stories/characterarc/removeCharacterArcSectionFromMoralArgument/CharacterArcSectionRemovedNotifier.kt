package com.soyle.stories.characterarc.removeCharacterArcSectionFromMoralArgument

import com.soyle.stories.usecase.character.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemoved
import com.soyle.stories.common.Notifier

class CharacterArcSectionRemovedNotifier : CharacterArcSectionRemovedReceiver,
    Notifier<CharacterArcSectionRemovedReceiver>() {

    override suspend fun receiveCharacterArcSectionRemoved(event: CharacterArcSectionRemoved) {
        notifyAll { it.receiveCharacterArcSectionRemoved(event) }
    }

}