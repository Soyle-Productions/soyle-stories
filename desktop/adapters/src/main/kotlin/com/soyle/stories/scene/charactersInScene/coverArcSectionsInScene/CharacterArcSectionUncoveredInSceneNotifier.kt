package com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene

class CharacterArcSectionUncoveredInSceneNotifier : CharacterArcSectionUncoveredInSceneReceiver, Notifier<CharacterArcSectionUncoveredInSceneReceiver>() {

    override suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>) {
        notifyAll { it.receiveCharacterArcSectionUncoveredInScene(events) }
    }

}