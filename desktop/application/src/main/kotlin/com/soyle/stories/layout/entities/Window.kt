package com.soyle.stories.layout.entities

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.layout.tools.ToolType
import java.util.*

class Window private constructor(
  override val id: Id,
  val layoutId: Layout.Id,
  private val _child: MutableWindowChild,
  @Suppress("UNUSED_PARAMETER") privateConstructorMarker: Unit = Unit
) : Entity<Window.Id> {

    constructor(id: Id, layoutId: Layout.Id, child: ToolStack) : this(id, layoutId, child, Unit)
    constructor(id: Id, layoutId: Layout.Id, child: StackSplitter) : this(id, layoutId, child, Unit)
    constructor(layoutId: Layout.Id) : this(Id(), layoutId, ToolStack(layoutId))

    private fun copy(
      child: MutableWindowChild = _child
    ) = Window(id, layoutId, child)

    val child: WindowChild
      get() = _child

    val isOpen: Boolean
        get() = child.isOpen

    fun hasTool(toolId: Tool.Id): Boolean = child.hasTool(toolId)
    fun hasToolStack(stackId: ToolStack.Id): Boolean = _child.hasToolStack(stackId)
    internal val tools: List<Tool>
        get() = _child.tools
    internal val toolStacks: List<ToolStack>
        get() = _child.toolStacks

    internal fun getToolStackForToolType(toolType: ToolType): ToolStack?
    {
        return _child.getToolStackForToolType(toolType)
    }

    internal fun withToolClosed(tool: Tool.Id): Window = copy(
      _child.withToolClosed(tool)
    )

    internal fun withToolOpened(tool: Tool.Id): Window = copy(
      _child.withToolOpened(tool)
    )

    internal fun withToolAddedToStack(tool: Tool, stackId: ToolStack.Id): Window {
        if (! hasToolStack(stackId)) return this
        return Window(
          id,
          layoutId,
          _child.withToolAddedToStack(tool, stackId)
        )
    }
    fun withoutTools(toolIds: Set<Tool.Id>): Window
    {
        return Window(
          id,
          layoutId,
          _child.withoutTools(toolIds)
        )
    }

    interface WindowChild {
        val id: Id
        val isOpen: Boolean
        val tools: List<Tool>
        fun hasTool(toolId: Tool.Id): Boolean
        fun hasToolStack(stackId: ToolStack.Id): Boolean

        interface Id
    }

    internal interface MutableWindowChild : WindowChild {
        val toolStacks: List<ToolStack>
        fun getToolStackForToolType(toolType: ToolType): ToolStack?
        fun withToolClosed(tool: Tool.Id): MutableWindowChild
        fun withToolOpened(tool: Tool.Id): MutableWindowChild
        fun withToolAddedToStack(tool: Tool, stackId: ToolStack.Id): MutableWindowChild
        fun withoutTools(toolIds: Set<Tool.Id>): MutableWindowChild
    }

    /**
     * START OF DEPRECIATED API
     */
/*
    val isPrimary: Boolean
        get() = child.isPrimary

    val primaryStack: ToolStack? by lazy {
        if (child.isPrimary) child.primaryStack
        else null
    }



    val markers: List<TempToolMarker> by lazy {
        child.markers
    }

    fun isToolOpen(toolId: Tool.Id): Boolean = child.isToolOpen(toolId)
    fun getParentToolGroup(toolId: Tool.Id): ToolStack? = child.getParentToolGroup(toolId)
    fun focusOnTool(toolId: Tool.Id): Window {
        return Window(id, layoutId, child.focusOnTool(toolId))
    }
    fun hasMarker(marker: TempToolMarker): Boolean = child.hasMarker(marker)
    fun addToolNextToMarker(tool: Tool<*>, marker: TempToolMarker): Window {
        return Window(id, layoutId, child.addToolNextToMarker(tool, marker))
    }

    internal fun withTemporaryToolMarkerOnPrimaryToolStack(marker: TempToolMarker): Window
    {
        return if (! isPrimary) this
        else Window(id, layoutId, child.withTemporaryToolMarkerOnPrimaryToolStack(marker))
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

    fun reOpenTool(toolType: KClass<out Tool<*>>, data: Any?): Window {
        return Window(id, layoutId, child.reOpenTool(toolType, data))
    }
*/

    class Id(val uuid: UUID = UUID.randomUUID()) {
        override fun equals(other: Any?): Boolean = other is Id && other.uuid == uuid
        override fun hashCode(): Int = uuid.hashCode()
        override fun toString(): String = "Window($uuid)"
    }
}