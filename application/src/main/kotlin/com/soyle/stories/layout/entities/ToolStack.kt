package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import java.util.*

class ToolStack(
    override val id: Id,
    val layoutId: Layout.Id,
    override val tools: List<Tool<*>>,
    override val isPrimary: Boolean,
    val focusedTool: Tool.Id?
) : Entity<ToolStack.Id>,
    Window.WindowChild {

    private val toolIds by lazy { tools.map(Tool<*>::id).toSet() }
    override val isOpen: Boolean by lazy { isPrimary || tools.any { it.isOpen } }
    override val staticTools: List<Tool<*>> by lazy { tools.filter { it.associatedData == null } }

    override fun isToolOpen(toolId: Tool.Id): Boolean = hasTool(toolId) && tools.find { it.id == toolId && it.isOpen } != null

    override fun hasTool(toolId: Tool.Id): Boolean = toolIds.contains(toolId)

    override fun getParentToolGroup(toolId: Tool.Id): ToolStack? {
        if (tools.find { it.id == toolId } != null) return this
        return null
    }

    override fun focusOnTool(toolId: Tool.Id): ToolStack {
        if (tools.find { it.id == toolId } == null) return this
        return ToolStack(id, layoutId, tools, isPrimary, toolId)
    }

    override fun openTool(toolId: Tool.Id): ToolStack {
        if (isToolOpen(toolId)) return this

        return ToolStack(
            id,
            layoutId,
            tools.map { if (it.id == toolId) it.open() else it },
            isPrimary,
            focusedTool
        )
    }

    override fun closeTool(toolId: Tool.Id): ToolStack {
        if (!isToolOpen(toolId)) return this

        return ToolStack(
            id,
            layoutId,
            tools.map { if (it.id == toolId) it.close() else it },
            isPrimary,
            null
        )
    }

    override fun toolRemoved(toolId: Tool.Id): Window.WindowChild {
        if (! hasTool(toolId)) return this
        val newTools = tools.filterNot { it.id == toolId }
        return ToolStack(
            id, layoutId, newTools, isPrimary, if (focusedTool == toolId) newTools.firstOrNull()?.id else focusedTool
        )
    }

    override fun openToolInPrimaryStack(tool: Tool<*>): Window.WindowChild {
        if (!isPrimary) return this
        return ToolStack(id, layoutId, tools + tool, isPrimary, null)
    }

    override fun reOpenTool(toolType: ToolType, data: Any?): Window.WindowChild {
        if (tools.find { it.type == toolType && !it.isOpen } == null) return this
        val newTools = tools.map {
            if (it.type == toolType && ((data == null && it.associatedData == null) || it.associatedData == data))
                it.open()
            else
                it
        }
        return ToolStack(id, layoutId, newTools, isPrimary, null)
    }

    class Id(val uuid: UUID){
        override fun toString(): String = "Stack($uuid)"
    }
}