package com.soyle.stories.layout.usecases.closeTool

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.Context
import com.soyle.stories.layout.LayoutException
import com.soyle.stories.layout.ToolDoesNotExist
import com.soyle.stories.layout.entities.*
import com.soyle.stories.layout.usecases.closeTool.CloseTool.OutputPort
import com.soyle.stories.layout.usecases.closeTool.CloseTool.ResponseModel
import java.util.*

class CloseToolUseCase(
    private val context: Context,
    projectId: UUID
) : CloseTool {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(toolId: UUID, output: OutputPort) {
        val response = try {
            closeTool(toolId)
        } catch (l: LayoutException) {
            return output.receiveCloseToolFailure(l)
        }
        output.receiveCloseToolResponse(response)
    }

    private suspend fun closeTool(toolId: UUID): ResponseModel {
        val layout = getLayoutWithTool(toolId)
            .closeToolAndSaveIfNotOpen(Tool.Id(toolId))
        return getResponseModel(layout, Tool.Id(toolId))
    }

    private suspend fun getLayoutWithTool(toolId: UUID) =
        context.layoutRepository
            .getLayoutForProject(projectId)
            ?.ensureHasTool(toolId)
            ?: throw ToolDoesNotExist(toolId)

    private suspend fun Layout.closeToolAndSaveIfNotOpen(toolId: Tool.Id): Layout {
        return if (isToolOpen(toolId)) {
            closeTool(toolId).fold(
                { throw it },
                {
                    context.layoutRepository.saveLayout(it)
                    it
                }
            )
        } else this
    }

    private fun getResponseModel(layout: Layout, toolId: Tool.Id): ResponseModel {
        val ancestorWindow = layout.windows.find { it.hasTool(toolId) }
        return ResponseModel(
            toolId.uuid,
            closedStackId = layout.getParentToolGroupIdIfClosed(toolId)?.uuid,
            closedSplitterIds = ancestorWindow?.getClosedAncestorSplitterIds(toolId).mapToUUIDsOrEmptyIfNull().toSet(),
            closedWindowId = ancestorWindow?.takeUnless { it.isOpen }?.id?.uuid
        )
    }

    private fun Layout.getParentToolGroupIdIfClosed(toolId: Tool.Id): ToolStack.Id? {
        return getParentToolGroup(toolId)?.takeUnless { it.isOpen }?.id
    }

    private fun Window.getClosedAncestorSplitterIds(toolId: Tool.Id): List<StackSplitter.Id>
    {
        return child.getAncestorSplittersFor(toolId).filter { ! it.isOpen }.map { it.id }
    }

    private fun List<StackSplitter.Id>?.mapToUUIDsOrEmptyIfNull() =
        this?.map { it.uuid } ?: emptyList()

    private fun Window.WindowChild.getAncestorSplittersFor(toolId: Tool.Id): List<StackSplitter> {
        return if (this is StackSplitter && this.hasTool(toolId)) {
            listOf(this) + children.flatMap {
                it.second.getAncestorSplittersFor(toolId)
            }
        } else emptyList()
    }


    private fun Layout.ensureHasTool(toolId: UUID): Layout? = takeIf { it.tools.find { it.id.uuid == toolId } != null }
}