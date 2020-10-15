package com.soyle.stories.character.createArcSection

import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.CreatedCharacterArcSection

interface CreatedCharacterArcSectionReceiver {

    suspend fun receiveCreatedCharacterArcSection(event: CreatedCharacterArcSection)

}