package com.soyle.stories.layout

import com.soyle.stories.entities.Project
import com.soyle.stories.layout.entities.Layout
import com.soyle.stories.layout.entities.Tool
import com.soyle.stories.layout.repositories.LayoutRepository

class TestContext(
    initialLayouts: List<Layout> = emptyList(),

    private val saveLayout: (Layout) -> Unit = {}
) : Context {

    private val _persistedItems = mutableListOf<PersistenceLog>()
    val persistedItems: List<PersistenceLog>
        get() = _persistedItems

    override val layoutRepository: LayoutRepository = object : LayoutRepository {
        override fun getLayoutContainingTool(toolId: Tool.Id): Layout? = initialLayouts.find {
            it.tools.find { it.id == toolId } != null
        }

        override fun getLayoutsContainingToolIds(toolIds: Set<Tool.Id>): List<Layout> = initialLayouts.filter {
            it.tools.find { it.id in toolIds } != null
        }

        override suspend fun getLayoutForProject(projectId: Project.Id): Layout? = initialLayouts.find { it.projectId == projectId }

        override suspend fun saveLayout(layout: Layout) {
            _persistedItems.add(PersistenceLog("saveLayout", layout))
            saveLayout.invoke(layout)
        }
    }

    data class PersistenceLog(val type: String, val data: Any) {
        override fun toString(): String {
            return "$type -> $data)"
        }
    }

}