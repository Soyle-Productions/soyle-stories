package com.soyle.stories.project.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 6:14 PM
 */
class ToggleToolOpenedNotifier : ToggleToolOpened.OutputPort, Notifier<ToggleToolOpened.OutputPort>() {

    override fun receiveToggleToolOpenedResponse(response: ToggleToolOpened.ResponseModel) {
        notifyAll {
            it.receiveToggleToolOpenedResponse(response)
        }
    }

}