package com.soyle.stories.character.buildNewCharacter

import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.common.Notifier
import kotlin.coroutines.coroutineContext

class CreatedCharacterNotifier : CreatedCharacterReceiver, Notifier<CreatedCharacterReceiver>() {
    override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
        notifyAll(coroutineContext) { it.receiveCreatedCharacter(createdCharacter) }
    }
}