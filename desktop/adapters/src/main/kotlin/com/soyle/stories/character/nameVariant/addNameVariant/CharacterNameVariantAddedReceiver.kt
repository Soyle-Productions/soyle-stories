package com.soyle.stories.character.nameVariant.addNameVariant

import com.soyle.stories.domain.character.name.events.CharacterNameAdded

interface CharacterNameVariantAddedReceiver {
    suspend fun receiveCharacterNameVariantAdded(event: CharacterNameAdded)
}