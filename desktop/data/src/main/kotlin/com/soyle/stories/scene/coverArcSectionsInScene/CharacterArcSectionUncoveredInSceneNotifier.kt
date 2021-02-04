package com.soyle.stories.scene.coverArcSectionsInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene

class CharacterArcSectionUncoveredInSceneNotifier : CharacterArcSectionUncoveredInSceneReceiver, Notifier<CharacterArcSectionUncoveredInSceneReceiver>() {

    override suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>) {
        notifyAll { it.receiveCharacterArcSectionUncoveredInScene(events) }
    }

}