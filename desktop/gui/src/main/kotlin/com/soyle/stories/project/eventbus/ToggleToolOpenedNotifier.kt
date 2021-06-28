package com.soyle.stories.project.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:14 PM
 */
class ToggleToolOpenedNotifier(
    private val threadTransformer: ThreadTransformer
) : ToggleToolOpened.OutputPort, Notifier<ToggleToolOpened.OutputPort>() {

    override fun receiveToggleToolOpenedResponse(response: GetSavedLayout.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveToggleToolOpenedResponse(response) }
        }
    }

    override fun failedToToggleToolOpen(failure: Throwable) {
        threadTransformer.async {
            notifyAll { it.failedToToggleToolOpen(failure) }
        }
    }

}