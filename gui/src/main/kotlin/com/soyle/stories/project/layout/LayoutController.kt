package com.soyle.stories.project.layout

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.layout.usecases.closeTool.CloseTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toggleToolOpened.ToggleToolOpened
import java.util.*
import kotlin.reflect.KClass

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
    private val closeToolOutputPort: CloseTool.OutputPort,
    private val layoutPresenter: LayoutPresenter
) : LayoutViewListener {

    override fun loadLayoutForProject(projectId: UUID) {
        threadTransformer.async {
            getSavedLayout.invoke(projectId, getSavedLayoutOutputPort)
        }
    }

    override suspend fun toggleToolOpen(tool: FixedTool) {
        toggleToolOpened.invoke(tool, toggleToolOpenedOutputPort)
    }

    override suspend fun closeTool(toolId: String) {
        threadTransformer.async {
            closeTool.invoke(UUID.fromString(toolId), closeToolOutputPort)
        }
    }

    override fun openDialog(dialog: Dialog) {

        layoutPresenter.displayDialog(dialog)
    }

    override fun closeDialog(dialog: KClass<out Dialog>) {
        layoutPresenter.removeDialog(dialog)
    }
}
