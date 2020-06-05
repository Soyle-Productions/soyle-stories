package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.layout.tools.ToolType
import java.util.*

class ToolStack(
    override val id: Id,
    val layoutId: Layout.Id,
    override val tools: List<Tool>,
    val markers: List<String>,
    val isPrimary: Boolean,
    val focusedTool: Tool.Id?
) : Entity<ToolStack.Id>,
    Window.MutableWindowChild {

    constructor(layoutId: Layout.Id) : this(Id(), layoutId, listOf(), listOf(), true, null)

    private fun copy(
      tools: List<Tool> = this.tools,
      markers: List<String> = this.markers,
      focusedTool: Tool.Id? = this.focusedTool
    ) = ToolStack(id, layoutId, tools, markers, isPrimary, focusedTool)

    override val isOpen: Boolean by lazy { isPrimary || tools.any { it.isOpen } }

    override val toolStacks: List<ToolStack>
        get() = listOf(this)

    private val toolIds by lazy { tools.map(Tool::id).toSet() }
    override fun hasTool(toolId: Tool.Id): Boolean = toolIds.contains(toolId)
    override fun hasToolStack(stackId: Id): Boolean = id == stackId
    private val toolTypes by lazy { tools.map(Tool::type).toSet() }
    override fun getToolStackForToolType(toolType: ToolType): ToolStack? {
        if (toolTypes.contains(toolType)) return this
        return null
    }

    override fun withToolClosed(tool: Tool.Id): ToolStack {
        if (! hasTool(tool)) return this
        return copy(tools = tools.map {
            if (it.id == tool) it.closed()
            else it
        })
    }

    override fun withToolOpened(tool: Tool.Id): ToolStack {
        if (! hasTool(tool)) return this
        return copy(tools = tools.map {
            if (it.id == tool) it.opened()
            else it
        })
    }

    override fun withToolAddedToStack(tool: Tool, stackId: Id): ToolStack {
        if (id != stackId) return this
        return copy(tools = tools + tool)
    }

    override fun withoutTools(toolIds: Set<Tool.Id>): ToolStack {
        if (this.toolIds.intersect(toolIds).isEmpty()) return this
        return copy(tools = tools.filterNot { it.id in toolIds })
    }

    /*

    private val toolIds by lazy { tools.map(Tool::id).toSet() }
    override val isOpen: Boolean by lazy { isPrimary || tools.any { it.isOpen } }
    override val primaryStack: ToolStack?
        get() = this.takeIf { it.isPrimary }
    override val staticTools: List<Tool<*>> by lazy { tools.filter { it.associatedData == null } }

    override fun isToolOpen(toolId: Tool.Id): Boolean = hasTool(toolId) && tools.find { it.id == toolId && it.isOpen } != null

    override fun hasTool(toolId: Tool.Id): Boolean = toolIds.contains(toolId)

    override fun getParentToolGroup(toolId: Tool.Id): ToolStack? {
        if (tools.find { it.id == toolId } != null) return this
        return null
    }

    override fun focusOnTool(toolId: Tool.Id): ToolStack {
        if (tools.find { it.id == toolId } == null) return this
        return copy(focusedTool = toolId)
    }

    override fun openTool(toolId: Tool.Id): ToolStack {
        if (isToolOpen(toolId)) return this

        return copy(tools = tools.map { if (it.id == toolId) it.open() else it })
    }

    override fun closeTool(toolId: Tool.Id): ToolStack {
        if (!isToolOpen(toolId)) return this

        return copy(tools = tools.map { if (it.id == toolId) it.close() else it })
    }

    override fun toolRemoved(toolId: Tool.Id): Window.WindowChild {
        if (! hasTool(toolId)) return this
        val newTools = tools.filterNot { it.id == toolId }
        return copy(tools = newTools, focusedTool = if (focusedTool == toolId) newTools.firstOrNull()?.id else focusedTool)
    }

    override fun openToolInPrimaryStack(tool: Tool<*>): Window.WindowChild {
        if (!isPrimary) return this
        return copy(tools = tools + tool, focusedTool = tool.id)
    }

    override fun reOpenTool(toolType: KClass<out Tool<*>>, data: Any?): Window.WindowChild {
        val tool = tools.find { toolType.isInstance(it) && !it.isOpen } ?: return this
        val newTools = tools.map {
            if (it isSameEntityAs tool)
                it.open()
            else
                it
        }
        return copy(tools = newTools, focusedTool = tool.id)
    }

    override fun withTemporaryToolMarkerOnPrimaryToolStack(marker: TempToolMarker): ToolStack
    {
        return if(! isPrimary) this
        else copy(markers = markers + marker)
    }
    private val _hasMarkerCache by lazy { markers.toSet() }
    override fun hasMarker(marker: TempToolMarker): Boolean = _hasMarkerCache.contains(marker)
    override fun addToolNextToMarker(tool: Tool<*>, marker: TempToolMarker): ToolStack {
        if (! hasMarker(marker)) return this
        return copy(
          tools = tools + tool
        )
    }*/

    class Id(val uuid: UUID = UUID.randomUUID()) : Window.WindowChild.Id {
        override fun toString(): String = "Stack($uuid)"
    }
}