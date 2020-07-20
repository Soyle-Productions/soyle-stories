package com.soyle.stories.character.deleteCharacterArc

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter

class DeleteCharacterArcNotifier : Notifier<DemoteMajorCharacter.OutputPort>(), DemoteMajorCharacter.OutputPort {

    override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
        notifyAll { it.receiveDemoteMajorCharacterResponse(response) }
    }

    override fun receiveDemoteMajorCharacterFailure(failure: Exception) {
        notifyAll { it.receiveDemoteMajorCharacterFailure(failure) }
    }
}