/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 1:46 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue

class ChangeThematicSectionValueNotifier : ChangeThematicSectionValue.OutputPort, Notifier<ChangeThematicSectionValue.OutputPort>() {
    override fun receiveChangeThematicSectionValueFailure(failure: Exception) {
        notifyAll { it.receiveChangeThematicSectionValueFailure(failure) }
    }

    override fun receiveChangeThematicSectionValueResponse(response: ChangeThematicSectionValue.ResponseModel) {
        notifyAll { it.receiveChangeThematicSectionValueResponse(response) }
    }
}