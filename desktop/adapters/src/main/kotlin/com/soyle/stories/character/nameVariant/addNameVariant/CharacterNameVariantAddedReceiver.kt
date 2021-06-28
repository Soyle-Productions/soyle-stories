package com.soyle.stories.character.nameVariant.addNameVariant

import com.soyle.stories.domain.character.events.CharacterNameVariantAdded

interface CharacterNameVariantAddedReceiver {
    suspend fun receiveCharacterNameVariantAdded(event: CharacterNameVariantAdded)
}