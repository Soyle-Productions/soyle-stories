package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.characterarc.planNewCharacterArc.CreatedCharacterArcReceiver
import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter

class PromoteMinorCharacterOutput(
    private val createdCharacterArcReceiver: CreatedCharacterArcReceiver
) : PromoteMinorCharacter.OutputPort {
    
    override suspend fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
        createdCharacterArcReceiver.receiveCreatedCharacterArc(response.createdCharacterArc)
    }
}