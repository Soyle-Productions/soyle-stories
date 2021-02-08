package com.soyle.stories.layout.doubles

import com.soyle.stories.domain.project.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository

class LayoutRepositoryDouble(
  private val onSaveLayout: (Layout) -> Unit = {}
) : LayoutRepository {

	var layout: Layout? = null

	override suspend fun getLayoutForProject(projectId: Project.Id): Layout? = layout?.takeIf {
		it.projectId == projectId
	}

	override suspend fun saveLayout(layout: Layout) {
		this.layout = layout
		onSaveLayout.invoke(layout)
	}

	override fun getLayoutContainingTool(toolId: Tool.Id): Layout? = layout?.takeIf {
		it.hasTool(toolId)
	}

	override fun getLayoutsContainingToolIds(toolIds: Set<Tool.Id>): List<Layout> = listOfNotNull(layout?.takeIf {
		toolIds.any { id ->
			it.hasTool(id)
		}
	})

}