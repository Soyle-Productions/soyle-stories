package com.soyle.stories.character.renameCharacter

import com.soyle.stories.domain.character.name.events.CharacterRenamed

fun interface CharacterRenamedReceiver {

    suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed)

}