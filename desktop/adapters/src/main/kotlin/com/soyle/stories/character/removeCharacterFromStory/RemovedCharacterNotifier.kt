package com.soyle.stories.character.removeCharacterFromStory

import com.soyle.stories.usecase.character.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.common.Notifier
import kotlin.coroutines.coroutineContext

class RemovedCharacterNotifier : RemovedCharacterReceiver, Notifier<RemovedCharacterReceiver>() {

    override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
        notifyAll { it.receiveCharacterRemoved(characterRemoved) }
    }

}