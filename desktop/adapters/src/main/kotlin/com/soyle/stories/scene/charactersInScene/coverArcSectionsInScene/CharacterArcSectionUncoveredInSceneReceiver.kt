package com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene

import com.soyle.stories.usecase.scene.charactersInScene.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene

interface CharacterArcSectionUncoveredInSceneReceiver {

    suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>)

}