package com.soyle.stories.layout.usecases.closeTool

import com.soyle.stories.layout.LayoutException
import java.util.*

interface CloseTool {

    suspend operator fun invoke(toolId: UUID, output: OutputPort)

    class ResponseModel(val toolId: UUID, val closedStackId: UUID?, val closedSplitterIds: Set<UUID>, val closedWindowId: UUID?)

    interface OutputPort {
        fun receiveCloseToolFailure(failure: LayoutException)
        fun receiveCloseToolResponse(response: ResponseModel)
    }

}