package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.character.buildNewCharacter.CharacterCreated

class CharacterCreatedNotifier : CharacterCreatedReceiver, Notifier<CharacterCreatedReceiver>() {
    override suspend fun receiveCreatedCharacter(characterCreated: CharacterCreated) {
        notifyAll { it.receiveCreatedCharacter(characterCreated) }
    }
}