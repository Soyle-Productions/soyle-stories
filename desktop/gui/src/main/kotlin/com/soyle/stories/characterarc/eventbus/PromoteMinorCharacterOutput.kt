package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcReceiver
import com.soyle.stories.usecase.theme.promoteMinorCharacter.PromoteMinorCharacter

class PromoteMinorCharacterOutput(
    private val createdCharacterArcReceiver: CreatedCharacterArcReceiver
) : PromoteMinorCharacter.OutputPort {
    
    override suspend fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
        createdCharacterArcReceiver.receiveCreatedCharacterArc(response.createdCharacterArc)
    }
}