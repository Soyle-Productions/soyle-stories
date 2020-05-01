/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 11:22 AM
 */
package com.soyle.stories.layout.usecases.openTool

import arrow.core.Either
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.entities.*
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.usecases.ActiveToolGroup
import com.soyle.stories.layout.usecases.toActiveTool
import java.util.*

class OpenToolUseCase(
    private val projectId: Project.Id,
    private val layoutRepository: LayoutRepository
) : OpenTool {

    override suspend fun invoke(requestModel: OpenTool.RequestModel, output: OpenTool.OutputPort) {
        val layout: Layout = layoutRepository.getLayoutForProject(projectId) ?: return output.receiveOpenToolFailure(
            LayoutDoesNotExist()
        )

        val toolType = when (requestModel) {
            is OpenTool.RequestModel.BaseStoryStructure -> ToolType.BaseStoryStructure
            is OpenTool.RequestModel.CharacterComparison -> ToolType.CharacterComparison
            is OpenTool.RequestModel.LocationDetails -> ToolType.LocationDetails
            else -> error("unsupported request model")
        }
        val identifyingData: Any? = when (requestModel) {
            is OpenTool.RequestModel.BaseStoryStructure -> (Theme.Id(requestModel.themeId) to Character.Id(requestModel.characterId))
            is OpenTool.RequestModel.CharacterComparison -> Theme.Id(requestModel.themeId)
            is OpenTool.RequestModel.LocationDetails -> Location.Id(requestModel.locationId)
            else -> error("unsupported request model")
        }
        val existingTool = layout.tools.find {
            it.type == toolType && it.identifyingData == identifyingData
        }
        val tool: Tool<*> = existingTool ?: when (requestModel) {
            is OpenTool.RequestModel.BaseStoryStructure -> BaseStoryStructureTool(Tool.Id(UUID.randomUUID()), Theme.Id(requestModel.themeId), Character.Id(requestModel.characterId), true)
            is OpenTool.RequestModel.CharacterComparison -> CharacterComparisonTool(Tool.Id(UUID.randomUUID()), Theme.Id(requestModel.themeId), Character.Id(requestModel.characterId), true)
            is OpenTool.RequestModel.LocationDetails -> LocationDetailsTool(Tool.Id(UUID.randomUUID()), identifyingData as Location.Id, true)
            else -> error("unsupported request model")
        }
        val exists: Boolean = existingTool != null

        val operationResult = when {
            ! exists -> layout.addToolToPrimaryStack(tool)
            else -> {
                val parentStack = layout.getParentToolGroup(tool.id)!!
                when {
                    ! tool.isOpen -> layout.openTool(tool.id)
                    parentStack.focusedTool != tool.id -> layout.focusOnTool(tool.id)
                    else -> return
                }
            }
        }

        if (operationResult is Either.Right) {
            layoutRepository.saveLayout(operationResult.b)
            val parentStack = operationResult.b.getParentToolGroup(tool.id)!!

            val differences = layout.difference(operationResult.b)

            OpenTool.ResponseModel(
                ActiveToolGroup(parentStack.id.uuid, parentStack.focusedTool?.uuid, parentStack.tools.filter { it.isOpen }.map { it.toActiveTool() }),
                    differences.second.map { it.id.uuid },
                    differences.first.firstOrNull()?.id?.uuid
            )
                .let(output::receiveOpenToolResponse)
        }

    }

    private fun Layout.difference(layout: Layout): Pair<List<Window>, List<StackSplitter>> {
        val windowsById = windows.associateBy { it.id }
        val differentWindows = layout.windows.filter {
            it.isOpen != windowsById.getValue(it.id).isOpen
        }
        return differentWindows to layout.windows.flatMap {
            val ogWindow = windowsById.getValue(it.id)
            ogWindow.difference(it)
        }
    }

    private fun Window.difference(window: Window): List<StackSplitter> {
        return child.difference(window.child)
    }

    private fun Window.WindowChild.difference(child: Window.WindowChild): List<StackSplitter> {
        return if (this is StackSplitter) {
            child as StackSplitter
            val childrenById = children.map { it.second }.filterIsInstance<StackSplitter>().associateBy { it.id }
            child.children.map { it.second }.filterIsInstance<StackSplitter>().flatMap {
                childrenById.getValue(it.id).difference(it)
            } + (if (isOpen != child.isOpen) listOf(child) else emptyList())
        }
        else emptyList()
    }

}