package com.soyle.stories.scene.coverArcSectionsInScene

import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene

interface CharacterArcSectionsCoveredBySceneReceiver {

    suspend fun receiveCharacterArcSectionsCoveredByScene(sections: List<CharacterArcSectionCoveredByScene>)

}