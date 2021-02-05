package com.soyle.stories.character.renameCharacter

import com.soyle.stories.entities.CharacterRenamed

interface CharacterRenamedReceiver {

    suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed)

}