package com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene

class CharacterArcSectionsCoveredBySceneNotifier : Notifier<CharacterArcSectionsCoveredBySceneReceiver>(),
    CharacterArcSectionsCoveredBySceneReceiver {

    override suspend fun receiveCharacterArcSectionsCoveredByScene(sections: List<CharacterArcSectionCoveredByScene>) {
        notifyAll { it.receiveCharacterArcSectionsCoveredByScene(sections) }
    }
}