/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 1:04 PM
 */
package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction

class ChangeStoryFunctionNotifier(private val threadTransformer: ThreadTransformer) : Notifier<ChangeStoryFunction.OutputPort>(), ChangeStoryFunction.OutputPort {
    override fun receiveChangeStoryFunctionFailure(failure: Exception) {
        threadTransformer.async {
            notifyAll { it.receiveChangeStoryFunctionFailure(failure) }
        }
    }

    override fun receiveChangeStoryFunctionResponse(response: ChangeStoryFunction.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveChangeStoryFunctionResponse(response) }
        }
    }
}