package com.soyle.stories.layout.closeTool

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import java.util.*

class CloseToolControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val closeTool: CloseTool,
    private val closeToolOutputPort: CloseTool.OutputPort
) : CloseToolController {

    override fun closeTool(toolId: String) {
        val formattedToolId = UUID.fromString(toolId)
        threadTransformer.async {
            closeTool.invoke(
                formattedToolId,
                closeToolOutputPort
            )
        }
    }
}