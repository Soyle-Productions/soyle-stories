package com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene

import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.CharacterArcSectionCoveredByScene

interface CharacterArcSectionsCoveredBySceneReceiver {

    suspend fun receiveCharacterArcSectionsCoveredByScene(sections: List<CharacterArcSectionCoveredByScene>)

}