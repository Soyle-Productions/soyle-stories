package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter

interface CreatedCharacterReceiver {
    suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter)
}