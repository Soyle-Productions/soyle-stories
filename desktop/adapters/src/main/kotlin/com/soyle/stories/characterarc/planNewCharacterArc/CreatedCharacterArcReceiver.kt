package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.usecase.character.planNewCharacterArc.CreatedCharacterArc

interface CreatedCharacterArcReceiver {
    suspend fun receiveCreatedCharacterArc(createdCharacterArc: CreatedCharacterArc)
}