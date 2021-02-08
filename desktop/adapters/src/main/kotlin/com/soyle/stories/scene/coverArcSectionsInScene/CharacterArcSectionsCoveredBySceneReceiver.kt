package com.soyle.stories.scene.coverArcSectionsInScene

import com.soyle.stories.usecase.scene.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene

interface CharacterArcSectionsCoveredBySceneReceiver {

    suspend fun receiveCharacterArcSectionsCoveredByScene(sections: List<CharacterArcSectionCoveredByScene>)

}