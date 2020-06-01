/**
 * Created by Brendan
 * Date: 3/5/2020
 * Time: 6:46 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter

class DeleteLocalCharacterArcNotifier : DemoteMajorCharacter.OutputPort, Notifier<DemoteMajorCharacter.OutputPort>() {
    override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
        notifyAll { it.receiveDemoteMajorCharacterResponse(response) }
    }

    override fun receiveDemoteMajorCharacterFailure(failure: Exception) {
        notifyAll { it.receiveDemoteMajorCharacterFailure(failure) }
    }
}