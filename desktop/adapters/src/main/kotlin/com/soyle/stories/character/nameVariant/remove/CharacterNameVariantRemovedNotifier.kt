package com.soyle.stories.character.nameVariant.remove

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved

class CharacterNameVariantRemovedNotifier : Notifier<CharacterNameVariantRemovedReceiver>(),
    CharacterNameVariantRemovedReceiver {

    override suspend fun receiveCharacterNameVariantRemoved(event: CharacterNameVariantRemoved) {
        notifyAll { it.receiveCharacterNameVariantRemoved(event) }
    }
}