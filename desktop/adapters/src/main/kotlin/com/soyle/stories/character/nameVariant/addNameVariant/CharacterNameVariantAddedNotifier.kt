package com.soyle.stories.character.nameVariant.addNameVariant

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.character.name.events.CharacterNameAdded

class CharacterNameVariantAddedNotifier : Notifier<CharacterNameVariantAddedReceiver>(), CharacterNameVariantAddedReceiver {

    override suspend fun receiveCharacterNameVariantAdded(event: CharacterNameAdded) {
        notifyAll { it.receiveCharacterNameVariantAdded(event) }
    }
}