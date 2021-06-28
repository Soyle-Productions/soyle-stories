package com.soyle.stories.character.nameVariant.remove

import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved

fun interface CharacterNameVariantRemovedReceiver {
    suspend fun receiveCharacterNameVariantRemoved(event: CharacterNameVariantRemoved)
}