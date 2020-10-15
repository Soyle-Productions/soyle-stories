package com.soyle.stories.character.createArcSection

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreatedCharacterArcSection

class CreatedCharacterArcSectionNotifier : Notifier<CreatedCharacterArcSectionReceiver>(), CreatedCharacterArcSectionReceiver {

    override suspend fun receiveCreatedCharacterArcSection(event: CreatedCharacterArcSection) {
        notifyAll { it.receiveCreatedCharacterArcSection(event) }
    }

}