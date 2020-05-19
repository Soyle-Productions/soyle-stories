package com.soyle.stories.layout.entities

import com.soyle.stories.common.Entity
import java.util.*
import kotlin.reflect.KClass

class StackSplitter(
    override val id: Id,
    val orientation: Boolean,
    val layoutId: Layout.Id,
    val children: List<Pair<Int, Window.WindowChild>>
) : Entity<StackSplitter.Id>,
    Window.WindowChild {

    override val isPrimary: Boolean by lazy { children.any { it.second.isPrimary } }
    override val isOpen: Boolean by lazy { children.any { it.second.isOpen } }
    override val staticTools: List<Tool<*>> by lazy { children.flatMap { it.second.staticTools } }
    override val tools: List<Tool<*>> by lazy { children.flatMap { it.second.tools } }

    init {
        if (children.filter { it.second.isPrimary }.size > 1) throw Error("May not have more than one primary stack in a layout.")
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

    override fun hasTool(toolId: Tool.Id): Boolean {
        return findTool(toolId).first
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


    class Id(val uuid: UUID){
        override fun toString(): String = "Splitter($uuid)"
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
}