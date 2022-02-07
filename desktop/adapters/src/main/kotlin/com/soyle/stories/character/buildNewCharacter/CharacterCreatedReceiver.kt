package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.usecase.character.buildNewCharacter.CharacterCreated

interface CharacterCreatedReceiver {
    suspend fun receiveCreatedCharacter(characterCreated: CharacterCreated)
}