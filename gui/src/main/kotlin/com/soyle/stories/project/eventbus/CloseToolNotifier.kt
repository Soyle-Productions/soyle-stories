package com.soyle.stories.project.eventbus

import com.soyle.stories.eventbus.Notifier
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.usecases.closeTool.CloseTool

class CloseToolNotifier : CloseTool.OutputPort, Notifier<CloseTool.OutputPort>() {
    override fun receiveCloseToolFailure(failure: LayoutException) {
        notifyAll { it.receiveCloseToolFailure(failure) }
    }

    override fun receiveCloseToolResponse(response: CloseTool.ResponseModel) {
        notifyAll { it.receiveCloseToolResponse(response) }
    }
}