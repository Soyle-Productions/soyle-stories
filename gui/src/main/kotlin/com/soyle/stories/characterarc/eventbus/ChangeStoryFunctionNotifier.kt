/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 1:04 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction

class ChangeStoryFunctionNotifier : Notifier<ChangeStoryFunction.OutputPort>(), ChangeStoryFunction.OutputPort {
    override fun receiveChangeStoryFunctionFailure(failure: Exception) {
        notifyAll { it.receiveChangeStoryFunctionFailure(failure) }
    }

    override fun receiveChangeStoryFunctionResponse(response: ChangeStoryFunction.ResponseModel) {
        notifyAll { it.receiveChangeStoryFunctionResponse(response) }
    }
}