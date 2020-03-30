package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import java.util.*

class Window(
    override val id: Id,
    val layoutId: Layout.Id,
    val child: WindowChild
) : Entity<Window.Id> {

    val isPrimary: Boolean
        get() = child.isPrimary

    val isOpen: Boolean
        get() = child.isOpen

    val tools: List<Tool<*>>
        get() = child.tools

    fun isToolOpen(toolId: Tool.Id): Boolean = child.isToolOpen(toolId)
    fun hasTool(toolId: Tool.Id): Boolean = child.hasTool(toolId)
    fun getParentToolGroup(toolId: Tool.Id): ToolStack? = child.getParentToolGroup(toolId)
    fun focusOnTool(toolId: Tool.Id): Window {
        return Window(id, layoutId, child.focusOnTool(toolId))
    }

    class Id(val uuid: UUID) {
        override fun equals(other: Any?): Boolean = other is Id && other.uuid == uuid
        override fun hashCode(): Int = uuid.hashCode()
        override fun toString(): String = "Window($uuid)"
    }

    interface WindowChild {
        val isPrimary: Boolean
        val isOpen: Boolean
        val tools: List<Tool<*>>
        val staticTools: List<Tool<*>>
        fun isToolOpen(toolId: Tool.Id): Boolean
        fun hasTool(toolId: Tool.Id): Boolean
        fun getParentToolGroup(toolId: Tool.Id): ToolStack?
        fun focusOnTool(toolId: Tool.Id): WindowChild

        fun openToolInPrimaryStack(tool: Tool<*>): WindowChild
        fun reOpenTool(toolType: ToolType, data: Any?): WindowChild
        fun closeTool(toolId: Tool.Id): WindowChild
        fun openTool(toolId: Tool.Id): WindowChild
        fun toolRemoved(toolId: Tool.Id): WindowChild
    }

    fun openToolInPrimaryStack(tool: Tool<*>): Window {
        if (! isPrimary) throw Error("This window is not the primary window")
        return Window(
            id,
            layoutId,
            child.openToolInPrimaryStack(tool)
        )
    }

    fun closeTool(toolId: Tool.Id): Window {
        if (! isToolOpen(toolId)) return this
        val newChild = child.closeTool(toolId)

        //val windowEvents = if (child.isOpen && ! newChild.isOpen) WindowClosed(layoutId, id).let(::listOf) else emptyList()

        return Window(id, layoutId, newChild)
    }

    fun openTool(toolId: Tool.Id): Window {
        if (isToolOpen(toolId)) return this
        val newChild = child.openTool(toolId)

        //val windowEvents = if (! child.isOpen && newChild.isOpen) WindowOpened(layoutId, id).let(::listOf) else emptyList()

        return Window(id, layoutId, newChild)
    }

    fun toolRemoved(toolId: Tool.Id): Window {
        if (! hasTool(toolId)) return this
        val newChild = child.toolRemoved(toolId)
        return Window(id, layoutId, newChild)
    }

    fun reOpenTool(toolType: ToolType, data: Any?): Window {
        return Window(id, layoutId, child.reOpenTool(toolType, data))
    }

}