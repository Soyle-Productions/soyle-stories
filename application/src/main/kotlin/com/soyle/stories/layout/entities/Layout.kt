package com.soyle.stories.layout.entities

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.common.Entity
import com.soyle.stories.entities.Project
import com.soyle.stories.layout.LayoutException
import java.util.*

class Layout(
    override val id: Id,
    val projectId: Project.Id,
    val windows: List<Window>
) : Entity<Layout.Id> {

    val tools: List<Tool<*>> by lazy {
        windows.flatMap { it.tools }
    }
    val staticTools: List<Tool<*>> by lazy {
        tools.filter { it.identifyingData is Project.Id }
    }

    init {
        windows.single { it.isPrimary }
    }

    fun closeTool(tool: Tool<*>): Either<*, Layout>
    {
        if (tool !in tools || ! tool.isOpen) return this.right()
        return try {
            val containingWindow = windows.find { it.hasTool(tool.id) } ?: error("")
            val modifiedWindow = containingWindow.closeTool(tool.id)
            Layout(id, projectId, windows.filterNot { it.id == modifiedWindow.id } + modifiedWindow).right()
        } catch (e: Exception)
        {
            e.left()
        }
    }

    fun closeTool(toolId: Tool.Id): Either<LayoutException, Layout>
    {
        val layout = toggleTool(toolId, false) ?: return this.right()
        return Layout(
            layout.id,
            projectId,
            layout.windows
        ).right()
    }

    fun openTool(toolId: Tool.Id): Either<*, Layout>
    {
        val layout = toggleTool(toolId, true) ?: return this.right()
        return Layout(
            layout.id,
            projectId,
            layout.windows
        ).right()
    }

    private fun toggleTool(toolId: Tool.Id, open: Boolean): Layout?
    {
        val windowWithTool = windows.find {
            it.hasTool(toolId)
        }
        if (windowWithTool == null || windowWithTool.isToolOpen(toolId) == open) {
            return null
        }
        val modifiedWindow = if (open) {
            windowWithTool.openTool(toolId)
        } else {
            windowWithTool.closeTool(toolId)
        }
        return Layout(
            id,
            projectId,
            windows.filterNot { it.id == modifiedWindow.id } + modifiedWindow
        )
    }

    fun toolRemoved(toolId: Tool.Id): Layout
    {
        val windowWithTool = windows.find {
            it.hasTool(toolId)
        } ?: return this

        val modifiedWindow = windowWithTool.toolRemoved(toolId)

        return Layout(
            id,
            projectId,
            windows.filterNot { it.id == modifiedWindow.id } + modifiedWindow
        )
    }

    fun getParentToolGroup(toolId: Tool.Id): ToolStack?
    {
        windows.forEach {
            it.getParentToolGroup(toolId)?.let { return it }
        }
        return null
    }

    fun focusOnTool(toolId: Tool.Id): Either<*, Layout>
    {
        val windowWithTool = windows.find {
            it.hasTool(toolId)
        } ?: return this.right()
        val modifiedWindow = windowWithTool.focusOnTool(toolId)
        return Layout(
            id,
            projectId,
            windows.filterNot { it.id == modifiedWindow.id } + modifiedWindow
        ).right()
    }

    fun isToolOpen(toolId: Tool.Id): Boolean
    {
        return windows.find {
            it.isToolOpen(toolId)
        } != null
    }

    fun addToolToPrimaryStack(tool: Tool<*>): Either<*, Layout>
    {
        val modifiedWindow = windows.find {
                it.isPrimary
            }?.openToolInPrimaryStack(tool)
        if (modifiedWindow == null) return this.right()
        return Layout(
            id, projectId, windows.filterNot { it.id == modifiedWindow.id } + modifiedWindow
        ).right()
    }

    class Id(val uuid: UUID) {
        override fun toString(): String = "Layout($uuid)"
    }

    companion object {
        fun organizeLayout(projectId: Project.Id): Either<*, Layout>
        {
            return defaultLayout(projectId, Id(UUID.randomUUID())).right()
        }
    }

}