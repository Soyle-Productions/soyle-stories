package com.soyle.stories.character.deleteCharacterArc

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.demoteMajorCharacter.DemoteMajorCharacter

class DeleteCharacterArcNotifier(
    private val threadTransformer: ThreadTransformer
) : Notifier<DemoteMajorCharacter.OutputPort>(), DemoteMajorCharacter.OutputPort {

    override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveDemoteMajorCharacterResponse(response) }
        }
    }

    override fun receiveDemoteMajorCharacterFailure(failure: Exception) {
        threadTransformer.async {
            notifyAll { it.receiveDemoteMajorCharacterFailure(failure) }
        }
    }
}