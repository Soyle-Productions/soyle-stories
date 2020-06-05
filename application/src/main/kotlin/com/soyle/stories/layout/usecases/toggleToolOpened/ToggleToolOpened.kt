package com.soyle.stories.layout.usecases.toggleToolOpened

import com.soyle.stories.layout.tools.fixed.FixedTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 5:16 PM
 */
interface ToggleToolOpened {

    suspend operator fun invoke(fixedTool: FixedTool, outputPort: OutputPort)

    interface OutputPort {
        fun failedToToggleToolOpen(failure: Throwable)
        fun receiveToggleToolOpenedResponse(response: GetSavedLayout.ResponseModel)
    }

}