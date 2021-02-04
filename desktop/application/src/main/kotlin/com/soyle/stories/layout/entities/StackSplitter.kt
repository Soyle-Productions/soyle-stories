package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import com.soyle.stories.layout.tools.ToolType
import java.util.*

class StackSplitter(
    override val id: Id,
    val orientation: Boolean,
    val layoutId: Layout.Id,
    val children: List<Pair<Int, Window.WindowChild>>
) : Entity<StackSplitter.Id>,
    Window.MutableWindowChild {

    private fun copy(
      children: List<Pair<Int, Window.WindowChild>> = this.children
    ) = StackSplitter(id, orientation, layoutId, children)

    override val isOpen: Boolean by lazy { children.any { it.second.isOpen } }
    override val tools: List<Tool> by lazy { children.flatMap { it.second.tools } }
    override val toolStacks: List<ToolStack> by lazy { children.flatMap { (it.second as Window.MutableWindowChild).toolStacks } }

    private val toolIds by lazy { tools.map(Tool::id).toSet() }
    private val toolStackIds by lazy { toolStacks.map(ToolStack::id).toSet() }
    override fun hasTool(toolId: Tool.Id): Boolean = toolIds.contains(toolId)
    override fun hasToolStack(stackId: ToolStack.Id): Boolean = toolStackIds.contains(stackId)
    override fun getToolStackForToolType(toolType: ToolType): ToolStack? {
        children.forEach {
            val stack = (it.second as Window.MutableWindowChild).getToolStackForToolType(toolType)
            if (stack != null) return stack
        }
        return null
    }

    override fun withToolClosed(tool: Tool.Id): StackSplitter {
        if (! hasTool(tool)) return this
        return copy(
          children = children.map {
              if (it.second.hasTool(tool)) it.first to (it.second as Window.MutableWindowChild).withToolClosed(tool)
              else it
          }
        )
    }

    override fun withToolOpened(tool: Tool.Id): StackSplitter {
        if (! hasTool(tool)) return this
        return copy(
          children = children.map {
              if (it.second.hasTool(tool)) it.first to (it.second as Window.MutableWindowChild).withToolOpened(tool)
              else it
          }
        )
    }

    override fun withToolAddedToStack(tool: Tool, stackId: ToolStack.Id): StackSplitter {
        if (! hasToolStack(stackId)) return this
        return copy(
          children = children.map {
              if (it.second.hasToolStack(stackId)) it.copy(second = (it.second as Window.MutableWindowChild).withToolAddedToStack(tool, stackId))
              else it
          }
        )
    }

    override fun withoutTools(toolIds: Set<Tool.Id>): StackSplitter {
        if (this.toolIds.intersect(toolIds).isEmpty()) return this
        return copy(
          children = children.map {
              it.copy(second = (it.second as Window.MutableWindowChild).withoutTools(toolIds))
          }
        )
    }
/*
    override val primaryStack: ToolStack? by lazy { children.find { it.second.isPrimary }?.second?.primaryStack }
    override val staticTools: List<Tool<*>> by lazy { children.flatMap { it.second.staticTools } }
    override val markers: List<TempToolMarker> by lazy {
        children.flatMap { it.second.markers }
    }


    private val findToolCache = mutableMapOf<Tool.Id, Pair<Boolean, Boolean>>()
    private fun findTool(toolId: Tool.Id): Pair<Boolean, Boolean>
    {
        return findToolCache.getOrPut(toolId) {
            val hasTool = children.find {
                it.second.hasTool(toolId)
            } != null
            val isToolOpen = children.find {
                it.second.isToolOpen(toolId)
            } != null
            hasTool to isToolOpen
        }
    }

    override fun isToolOpen(toolId: Tool.Id): Boolean {
        return findTool(toolId).second
    }

    override fun getParentToolGroup(toolId: Tool.Id): ToolStack? {
        children.forEach {
            it.second.getParentToolGroup(toolId)?.let { return it }
        }
        return null
    }

    override fun focusOnTool(toolId: Tool.Id): StackSplitter {
        if (! hasTool(toolId)) return this
        return StackSplitter(id, orientation, layoutId, children.map {
            it.first to it.second.focusOnTool(toolId)
        })
    }

    private fun toggleTool(toolId: Tool.Id, open: Boolean): StackSplitter
    {
        if (isToolOpen(toolId) == open) return this

        val newChildren = children
            .map { (weight, child) ->
                if (child.isToolOpen(toolId) != open) {
                    if (open) {
                        weight to child.openTool(toolId)
                    } else {
                        weight to child.closeTool(toolId)
                    }
                } else {
                    weight to child
                }
            }

        return StackSplitter(
            id,
            orientation,
            layoutId,
            newChildren
        )
    }

    override fun openTool(toolId: Tool.Id): StackSplitter {
        val newSplitter = toggleTool(toolId, true)

       // val splitterEvents = if (! isOpen && newSplitter.isOpen) GroupSplitterOpened(layoutId, id).let(::listOf) else emptyList()

        return newSplitter
    }

    override fun closeTool(toolId: Tool.Id): StackSplitter {
        val newSplitter = toggleTool(toolId, false)

       // val splitterEvents = if (isOpen && ! newSplitter.isOpen) GroupSplitterClosed(layoutId, id).let(::listOf) else emptyList()

        return newSplitter
    }

    override fun toolRemoved(toolId: Tool.Id): Window.WindowChild {
        if (! hasTool(toolId)) return this
        return StackSplitter(
            id,
            orientation, layoutId, children.map { it.first to it.second.toolRemoved(toolId) }
        )
    }

    override fun withTemporaryToolMarkerOnPrimaryToolStack(marker: TempToolMarker): StackSplitter {
        return if (! isPrimary) this
        else StackSplitter(
          id, orientation, layoutId, children.map { it.copy(second = it.second.withTemporaryToolMarkerOnPrimaryToolStack(marker)) }
        )
    }

    override fun openToolInPrimaryStack(tool: Tool<*>): Window.WindowChild {
        if (! isPrimary) return this
        return StackSplitter(
            id,
            orientation,
            layoutId,
            children.map { it.first to it.second.openToolInPrimaryStack(tool) })
    }

    override fun reOpenTool(toolType: KClass<out Tool<*>>, data: Any?): Window.WindowChild {
        return StackSplitter(
            id,
            orientation,
            layoutId,
            children.map { it.first to it.second.reOpenTool(toolType, data) })
    }

    private val _hasMarkerCache by lazy { markers.toSet() }
    override fun hasMarker(marker: TempToolMarker): Boolean = _hasMarkerCache.contains(marker)
    override fun addToolNextToMarker(tool: Tool<*>, marker: TempToolMarker): StackSplitter {
        if (! hasMarker(marker)) return this
        return StackSplitter(id, orientation, layoutId, children.map {
            if (! it.second.hasMarker(marker)) it
            else it.copy(second = it.second.addToolNextToMarker(tool, marker))
        })
    }
    */

    class Id(val uuid: UUID) : Window.WindowChild.Id {
        override fun toString(): String = "Splitter($uuid)"
    }
}