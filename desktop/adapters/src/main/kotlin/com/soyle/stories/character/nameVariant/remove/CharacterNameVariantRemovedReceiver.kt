package com.soyle.stories.character.nameVariant.remove

import com.soyle.stories.domain.character.name.events.CharacterNameRemoved

fun interface CharacterNameVariantRemovedReceiver {
    suspend fun receiveCharacterNameVariantRemoved(event: CharacterNameRemoved)
}