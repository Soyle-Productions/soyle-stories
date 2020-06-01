package com.soyle.stories.project.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout

class CloseToolNotifier : CloseTool.OutputPort, Notifier<CloseTool.OutputPort>() {
    override fun receiveCloseToolFailure(failure: LayoutException) {
        notifyAll { it.receiveCloseToolFailure(failure) }
    }

    override fun receiveCloseToolResponse(response: GetSavedLayout.ResponseModel) {
        notifyAll { it.receiveCloseToolResponse(response) }
    }
}