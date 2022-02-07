package com.soyle.stories.character.nameVariant.remove

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.character.name.events.CharacterNameRemoved

class CharacterNameVariantRemovedNotifier : Notifier<CharacterNameVariantRemovedReceiver>(),
    CharacterNameVariantRemovedReceiver {

    override suspend fun receiveCharacterNameVariantRemoved(event: CharacterNameRemoved) {
        notifyAll { it.receiveCharacterNameVariantRemoved(event) }
    }
}