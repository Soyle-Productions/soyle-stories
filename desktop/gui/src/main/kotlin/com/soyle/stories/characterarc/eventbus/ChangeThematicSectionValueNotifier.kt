/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 1:46 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.theme.changeThematicSectionValue.ChangeThematicSectionValue

class ChangeThematicSectionValueNotifier(
    private val threadTransformer: ThreadTransformer
) : ChangeThematicSectionValue.OutputPort, Notifier<ChangeThematicSectionValue.OutputPort>() {
    override fun receiveChangeThematicSectionValueFailure(failure: Exception) {
        threadTransformer.async {
            notifyAll { it.receiveChangeThematicSectionValueFailure(failure) }
        }
    }

    override fun receiveChangeThematicSectionValueResponse(response: ChangeThematicSectionValue.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveChangeThematicSectionValueResponse(response) }
        }
    }
}