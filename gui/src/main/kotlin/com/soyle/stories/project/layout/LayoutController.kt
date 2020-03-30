package com.soyle.stories.project.layout

import com.soyle.stories.gui.ThreadTransformer
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import java.util.*

/**
 * Created by Brendan
 * Date: 2/15/2020
 * Time: 2:31 PM
 */
class LayoutController(
    private val threadTransformer: ThreadTransformer,
    private val getSavedLayout: GetSavedLayout,
    private val getSavedLayoutOutputPort: GetSavedLayout.OutputPort,
    private val toggleToolOpened: ToggleToolOpened,
    private val toggleToolOpenedOutputPort: ToggleToolOpened.OutputPort,
    private val closeTool: CloseTool,
    private val closeToolOutputPort: CloseTool.OutputPort
) : LayoutViewListener {

    override suspend fun loadLayoutForProject(projectId: UUID) {
        getSavedLayout.invoke(projectId, getSavedLayoutOutputPort)
    }

    override suspend fun toggleToolOpen(toolId: String) {
        toggleToolOpened.invoke(UUID.fromString(toolId), toggleToolOpenedOutputPort)
    }

    override suspend fun closeTool(toolId: String) {
        threadTransformer.async {
            closeTool.invoke(UUID.fromString(toolId), closeToolOutputPort)
        }
    }
}
