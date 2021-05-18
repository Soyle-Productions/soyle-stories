package com.soyle.stories.character.nameVariant.addNameVariant

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.character.events.CharacterNameVariantAdded

class CharacterNameVariantAddedNotifier : Notifier<CharacterNameVariantAddedReceiver>(), CharacterNameVariantAddedReceiver {

    override suspend fun receiveCharacterNameVariantAdded(event: CharacterNameVariantAdded) {
        notifyAll { it.receiveCharacterNameVariantAdded(event) }
    }
}