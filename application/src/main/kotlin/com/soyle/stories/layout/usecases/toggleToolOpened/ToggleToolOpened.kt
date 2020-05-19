package com.soyle.stories.layout.usecases.toggleToolOpened

import com.soyle.stories.layout.usecases.OpenWindow
import com.soyle.stories.layout.usecases.StaticTool
import java.util.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 5:16 PM
 */
interface ToggleToolOpened {

    suspend operator fun invoke(toolId: UUID, outputPort: OutputPort)

    class ResponseModel(
	  val layoutId: UUID, val windows: List<OpenWindow>, val staticTools: List<StaticTool>
    )

    interface OutputPort {
        fun receiveToggleToolOpenedResponse(response: ResponseModel)
    }

}