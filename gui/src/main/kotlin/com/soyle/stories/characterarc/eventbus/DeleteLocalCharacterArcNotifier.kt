/**
 * Created by Brendan
 * Date: 3/5/2020
 * Time: 6:46 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.characterarc.LocalCharacterArcException
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.common.Notifier

class DeleteLocalCharacterArcNotifier : DeleteLocalCharacterArc.OutputPort, Notifier<DeleteLocalCharacterArc.OutputPort>() {
    override fun receiveDeleteLocalCharacterArcFailure(failure: LocalCharacterArcException) {
        notifyAll { it.receiveDeleteLocalCharacterArcFailure(failure) }
    }

    override fun receiveDeleteLocalCharacterArcResponse(response: DeleteLocalCharacterArc.ResponseModel) {
        notifyAll { it.receiveDeleteLocalCharacterArcResponse(response) }
    }
}