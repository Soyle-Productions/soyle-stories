package com.soyle.stories.character.renameCharacter

import com.soyle.stories.domain.character.CharacterRenamed

interface CharacterRenamedReceiver {

    suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed)

}