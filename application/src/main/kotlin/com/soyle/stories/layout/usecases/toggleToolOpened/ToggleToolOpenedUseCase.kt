package com.soyle.stories.layout.usecases.toggleToolOpened

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.LayoutDoesNotContainFixedTool
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toResponseModel
import java.util.*

class ToggleToolOpenedUseCase(
  projectId: UUID,
    private val layoutRepository: LayoutRepository
) : ToggleToolOpened {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(fixedTool: FixedTool, outputPort: ToggleToolOpened.OutputPort) {
        val response = try { execute(fixedTool) }
        catch (t: Throwable) {
            return outputPort.failedToToggleToolOpen(t)
        }
        outputPort.receiveToggleToolOpenedResponse(response)
    }

    private suspend fun execute(fixedTool: FixedTool): GetSavedLayout.ResponseModel {
        val layout = getLayout()
        val tool = layout.getTool(fixedTool)
        val modifiedLayout = toggleToolOpen(layout, tool)
        return modifiedLayout.toResponseModel()
    }

    private suspend fun getLayout()  = layoutRepository.getLayoutForProject(projectId)
      ?: throw LayoutDoesNotExist()

    private fun Layout.getTool(fixedTool: FixedTool): Tool = getToolByType(fixedTool)
      ?: throw LayoutDoesNotContainFixedTool(fixedTool)

    private suspend fun toggleToolOpen(layout: Layout, tool: Tool): Layout
    {
        val modifiedLayout = if (tool.isOpen) layout.withToolClosed(tool.id)
        else layout.withToolOpened(tool.id)
        layoutRepository.saveLayout(modifiedLayout)
        return modifiedLayout
    }

}