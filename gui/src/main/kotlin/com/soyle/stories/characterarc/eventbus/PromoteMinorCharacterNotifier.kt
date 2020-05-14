/**
 * Created by Brendan
 * Date: 3/5/2020
 * Time: 10:46 AM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter

class PromoteMinorCharacterNotifier : PromoteMinorCharacter.OutputPort, Notifier<PromoteMinorCharacter.OutputPort>() {
    override fun receivePromoteMinorCharacterFailure(failure: ThemeException) {
        notifyAll { it.receivePromoteMinorCharacterFailure(failure) }
    }

    override fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
        notifyAll { it.receivePromoteMinorCharacterResponse(response) }
    }
}