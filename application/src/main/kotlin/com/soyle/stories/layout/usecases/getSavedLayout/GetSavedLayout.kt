package com.soyle.stories.layout.usecases.getSavedLayout

import com.soyle.stories.layout.usecases.OpenWindow
import com.soyle.stories.layout.usecases.StaticTool
import java.util.*

/**
 * Created by Brendan
 * Date: 2/14/2020
 * Time: 11:13 PM
 */
interface GetSavedLayout {

    suspend operator fun invoke(projectId: UUID, outputPort: OutputPort)

    class ResponseModel(
	  val layoutId: UUID, val windows: List<OpenWindow>, val staticTools: List<StaticTool>
    )

    interface OutputPort {
        fun receiveGetSavedLayoutResponse(response: ResponseModel)
    }

}