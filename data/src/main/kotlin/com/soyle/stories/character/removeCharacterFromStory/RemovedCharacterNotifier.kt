package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.common.Notifier
import kotlin.coroutines.coroutineContext

class RemovedCharacterNotifier : RemovedCharacterReceiver, Notifier<RemovedCharacterReceiver>() {

    override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
        notifyAll(coroutineContext) { it.receiveCharacterRemoved(characterRemoved) }
    }

}