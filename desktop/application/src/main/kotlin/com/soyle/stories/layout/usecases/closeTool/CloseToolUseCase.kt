package com.soyle.stories.layout.usecases.closeTool

import com.soyle.stories.domain.project.Project
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.ToolDoesNotExist
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.usecases.closeTool.CloseTool.OutputPort
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toResponseModel
import java.util.*

class CloseToolUseCase(
  projectId: UUID,
  private val layoutRepository: LayoutRepository
) : CloseTool {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(toolId: UUID, output: OutputPort) {
        val response = try { execute(toolId) }
        catch (l: LayoutException) { return output.receiveCloseToolFailure(l) }
        output.receiveCloseToolResponse(response)
    }

    private suspend fun execute(toolId: UUID): GetSavedLayout.ResponseModel {
        val layout = getLayout()
        val tool = getTool(layout, toolId)
        val modifiedLayout = closeTool(layout, tool)
        return modifiedLayout.toResponseModel()
    }

    private suspend fun getLayout() =
        layoutRepository.getLayoutForProject(projectId)
            ?: throw LayoutDoesNotExist()

    private suspend fun closeTool(layout: Layout, tool: Tool): Layout {
        return if (layout.isToolOpen(tool.id)) {
            val modifiedLayout = if (tool.isTemporary()) layout.withoutTools(setOf(tool.id))
            else layout.withToolClosed(tool.id)
            layoutRepository.saveLayout(modifiedLayout)
            modifiedLayout
        } else layout
    }

    private fun getTool(layout: Layout, toolId: UUID) =
      layout.getToolById(Tool.Id(toolId))
        ?: throw ToolDoesNotExist(toolId)
}