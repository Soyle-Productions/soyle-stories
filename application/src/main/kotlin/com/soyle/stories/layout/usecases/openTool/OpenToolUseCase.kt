package com.soyle.stories.layout.usecases.openTool

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.dynamic.DynamicTool
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toResponseModel
import java.util.*

class OpenToolUseCase(
  projectId: UUID,
  private val layoutRepository: LayoutRepository,
  private val context: OpenToolContext
) : OpenTool {

    private val projectId = Project.Id(projectId)

    override suspend fun invoke(toolType: DynamicTool, output: OpenTool.OutputPort) {
        val response = try { execute(toolType) }
        catch (e: Exception){ return output.receiveOpenToolFailure(e) }
        output.receiveOpenToolResponse(response)
    }

    private suspend fun execute(toolType: DynamicTool): GetSavedLayout.ResponseModel {
        val layout = getLayout()
        toolType.validate(context)
        val existingTool = layout.getToolByType(toolType)
        val modifiedLayout = if (existingTool != null) {
            layout.withToolOpened(existingTool.id)
        } else {
            val tool = Tool(toolType, true)
            val stack = if (toolType.isTemporary) {
                layout.getToolStackWithMarkerForToolType(toolType)
                  ?: layout.primaryStack
            } else layout.primaryStack
            layout.withToolAddedToStack(tool, stack.id)
        }
        layoutRepository.saveLayout(modifiedLayout)
        return modifiedLayout.toResponseModel()
    }

    private suspend fun getLayout() = (layoutRepository.getLayoutForProject(projectId)
      ?: throw LayoutDoesNotExist())
}