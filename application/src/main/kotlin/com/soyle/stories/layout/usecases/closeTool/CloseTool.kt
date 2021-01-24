package com.soyle.stories.layout.usecases.closeTool

import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import java.util.*

interface CloseTool {

    suspend operator fun invoke(toolId: UUID, output: OutputPort)

    interface OutputPort {
        fun receiveCloseToolFailure(failure: LayoutException)
        fun receiveCloseToolResponse(response: GetSavedLayout.ResponseModel)
    }

}