package com.soyle.stories.scene.coverArcSectionsInScene

import com.soyle.stories.usecase.scene.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene

interface CharacterArcSectionUncoveredInSceneReceiver {

    suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>)

}