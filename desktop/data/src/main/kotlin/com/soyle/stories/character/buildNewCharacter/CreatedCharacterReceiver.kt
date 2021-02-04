package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter

interface CreatedCharacterReceiver {
    suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter)
}