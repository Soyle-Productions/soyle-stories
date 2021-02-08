package com.soyle.stories.layout.usecases.removeToolsWithId

import com.soyle.stories.domain.project.Project
import com.soyle.stories.layout.LayoutDoesNotExist
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository
import com.soyle.stories.layout.usecases.getSavedLayout.GetSavedLayout
import com.soyle.stories.layout.usecases.toResponseModel
import java.util.*

class RemoveToolsWithIdUseCase(
  projectId: UUID,
  private val layoutRepository: LayoutRepository
) : RemoveToolsWithId {

	private val projectId = Project.Id(projectId)

	override suspend fun invoke(id: UUID, output: RemoveToolsWithId.OutputPort) {
		val response = try { execute(id) }
		catch (e: Exception) { return output.failedToRemoveToolsWithId(e) }
		output.toolsRemovedWithId(response)
	}

	private suspend fun execute(id: UUID): GetSavedLayout.ResponseModel
	{
		val layout = getLayout()
		val tools = layout.getToolsWithIdInType(id)
		val modifiedLayout: Layout
		if (tools.isNotEmpty()) {
			modifiedLayout = layout.withoutTools(tools.map(Tool::id).toSet())
			layoutRepository.saveLayout(modifiedLayout)
		} else {
			modifiedLayout = layout
		}
		return modifiedLayout.toResponseModel()

	}

	private suspend fun getLayout() = (layoutRepository.getLayoutForProject(projectId)
	  ?: throw LayoutDoesNotExist())
}