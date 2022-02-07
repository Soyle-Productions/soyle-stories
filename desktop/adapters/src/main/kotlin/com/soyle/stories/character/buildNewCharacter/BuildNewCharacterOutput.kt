package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.usecase.character.buildNewCharacter.BuildNewCharacter
import com.soyle.stories.usecase.character.buildNewCharacter.CharacterCreated

class BuildNewCharacterOutput(
    private val characterCreatedReceiver: CharacterCreatedReceiver,
) : BuildNewCharacter.OutputPort {

    override suspend fun characterCreated(response: CharacterCreated) {
        characterCreatedReceiver.receiveCreatedCharacter(response)
    }

}