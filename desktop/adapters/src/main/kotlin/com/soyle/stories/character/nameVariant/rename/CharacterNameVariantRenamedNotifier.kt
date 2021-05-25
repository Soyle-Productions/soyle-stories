package com.soyle.stories.character.nameVariant.rename

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed

class CharacterNameVariantRenamedNotifier : CharacterNameVariantRenamedReceiver,
    Notifier<CharacterNameVariantRenamedReceiver>() {

    override suspend fun receiveCharacterNameVariantRenamed(event: CharacterNameVariantRenamed) {
        notifyAll { it.receiveCharacterNameVariantRenamed(event) }
    }
}