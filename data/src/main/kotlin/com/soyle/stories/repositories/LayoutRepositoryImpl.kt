package com.soyle.stories.repositories

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository

class LayoutRepositoryImpl : LayoutRepository {
	var layout: Layout? = null

	override suspend fun getLayoutForProject(projectId: Project.Id): Layout? = layout

	override suspend fun saveLayout(layout: Layout) { this.layout = layout }

	override fun getLayoutContainingTool(toolId: Tool.Id): Layout? = layout

	override fun getLayoutsContainingToolIds(toolIds: Set<Tool.Id>): List<Layout> = listOfNotNull(layout)
}