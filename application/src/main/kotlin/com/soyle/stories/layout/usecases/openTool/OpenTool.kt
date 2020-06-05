package com.soyle.stories.layout.usecases.openTool

import com.soyle.stories.layout.tools.dynamic.DynamicTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout

interface OpenTool {

    suspend operator fun invoke(toolType: DynamicTool, output: OutputPort)

    interface OutputPort {
        fun receiveOpenToolFailure(failure: Exception)
        fun receiveOpenToolResponse(response: GetSavedLayout.ResponseModel)
    }

}