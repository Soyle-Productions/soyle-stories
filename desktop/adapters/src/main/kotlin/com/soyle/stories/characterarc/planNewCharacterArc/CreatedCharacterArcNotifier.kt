package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.usecase.character.arc.planNewCharacterArc.CreatedCharacterArc
import com.soyle.stories.common.Notifier

class CreatedCharacterArcNotifier : Notifier<CreatedCharacterArcReceiver>(), CreatedCharacterArcReceiver {
    override suspend fun receiveCreatedCharacterArc(createdCharacterArc: CreatedCharacterArc) {
        notifyAll { it.receiveCreatedCharacterArc(createdCharacterArc) }
    }
}