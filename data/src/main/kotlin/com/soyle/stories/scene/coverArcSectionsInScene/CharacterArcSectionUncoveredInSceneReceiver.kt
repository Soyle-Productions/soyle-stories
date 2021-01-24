package com.soyle.stories.scene.coverArcSectionsInScene

import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene

interface CharacterArcSectionUncoveredInSceneReceiver {

    suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>)

}