package com.soyle.stories.project.eventbus

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout

class CloseToolNotifier(private val threadTransformer: ThreadTransformer) : CloseTool.OutputPort, Notifier<CloseTool.OutputPort>() {
    override fun receiveCloseToolFailure(failure: LayoutException) {
        threadTransformer.async {
            notifyAll { it.receiveCloseToolFailure(failure) }
        }
    }

    override fun receiveCloseToolResponse(response: GetSavedLayout.ResponseModel) {
        threadTransformer.async {
            notifyAll { it.receiveCloseToolResponse(response) }
        }
    }
}