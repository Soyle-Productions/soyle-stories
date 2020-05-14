/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 4:16 PM
 */
package com.soyle.stories.project.layout.openTool

import com.soyle.stories.common.Notifier
import com.soyle.stories.layout.usecases.openTool.OpenTool

class OpenToolNotifier : OpenTool.OutputPort, Notifier<OpenTool.OutputPort>() {
    override fun receiveOpenToolFailure(failure: Exception) {
        notifyAll { it.receiveOpenToolFailure(failure) }
    }

    override fun receiveOpenToolResponse(response: OpenTool.ResponseModel) {
        notifyAll { it.receiveOpenToolResponse(response) }
    }
}