package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.characterarc.usecases.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.common.Notifier
import kotlin.coroutines.coroutineContext

class CreatedCharacterArcNotifier : Notifier<CreatedCharacterArcReceiver>(), CreatedCharacterArcReceiver {
    override suspend fun receiveCreatedCharacterArc(createdCharacterArc: CreatedCharacterArc) {
        notifyAll { it.receiveCreatedCharacterArc(createdCharacterArc) }
    }
}