package com.soyle.stories.character.nameVariant.rename

import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed

fun interface CharacterNameVariantRenamedReceiver {
    suspend fun receiveCharacterNameVariantRenamed(event: CharacterNameVariantRenamed)
}