package com.soyle.stories.characterarc.planNewCharacterArc

import com.soyle.stories.characterarc.usecases.planNewCharacterArc.CreatedCharacterArc

interface CreatedCharacterArcReceiver {
    suspend fun receiveCreatedCharacterArc(createdCharacterArc: CreatedCharacterArc)
}