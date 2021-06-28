package com.soyle.stories.scene.charactersInScene.coverArcSectionsInScene

import com.soyle.stories.usecase.scene.character.coverCharacterArcSectionsInScene.CharacterArcSectionUncoveredInScene

interface CharacterArcSectionUncoveredInSceneReceiver {

    suspend fun receiveCharacterArcSectionUncoveredInScene(events: List<CharacterArcSectionUncoveredInScene>)

}