package com.soyle.stories.layout.entities

import com.soyle.stories.domain.entities.Entity
import com.soyle.stories.domain.project.Project
import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.layout.tools.FixedTool
import java.util.*

class Layout(
  override val id: Id,
  val projectId: Project.Id,
  val windows: List<Window>
) : Entity<Layout.Id> {

	private constructor(id: Id, projectId: Project.Id) : this(id, projectId, listOf(Window(id)))
	constructor(projectId: Project.Id) : this(Id(), projectId)

	private fun copy(
	  windows: List<Window> = this.windows
	) = Layout(id, projectId, windows)

	private val toolStacks: List<ToolStack> by lazy {
		windows.flatMap { it.toolStacks }
	}
	private val tools: List<Tool> by lazy {
		windows.flatMap { it.tools }
	}
	private val toolsByType: Map<ToolType, Tool> by lazy {
		tools.associateBy { it.type }
	}
	private val toolsById: Map<Tool.Id, Tool> by lazy {
		tools.associateBy { it.id }
	}

	val fixedTools: List<Tool> by lazy {
		tools.filter { it.type is FixedTool }
	}

	val primaryStack: ToolStack by lazy {
		toolStacks.single { it.isPrimary }
	}

	fun getToolByType(type: ToolType): Tool? = toolsByType[type]
	fun getToolById(toolId: Tool.Id) = toolsById[toolId]
	fun hasTool(toolId: Tool.Id) = toolsById.containsKey(toolId)
	fun isToolOpen(toolId: Tool.Id): Boolean = toolsById[toolId]?.isOpen ?: false
	fun getToolStackForToolType(toolType: ToolType): ToolStack? {
		windows.forEach {
			val stack = it.getToolStackForToolType(toolType)
			if (stack != null) return stack
		}
		return null
	}

	fun getToolStackWithMarkerForToolType(toolType: ToolType): ToolStack? =
	  toolStacks.find { it.markers.contains(toolType::class.qualifiedName) }

	fun getToolsWithIdInType(id: UUID): List<Tool>
	{
		return tools.filter { it.type.identifiedWithId(id) }
	}


	fun withToolClosed(tool: Tool.Id): Layout = copy(windows.map {
		if (it.hasTool(tool)) it.withToolClosed(tool)
		else it
	})

	fun withToolOpened(tool: Tool.Id): Layout = copy(windows.map {
		if (it.hasTool(tool)) it.withToolOpened(tool)
		else it
	})

	fun withToolAddedToStack(tool: Tool, stackId: ToolStack.Id): Layout {
		val window = windows.find { it.hasToolStack(stackId) }
		  ?: return this
		return Layout(
		  id, projectId, windows.map {
			if (it.id == window.id) it.withToolAddedToStack(tool, stackId)
			else it
          }
        )
	}

	fun withoutTools(toolIds: Set<Tool.Id>): Layout
	{
		return Layout(
		  id, projectId, windows.map {
			it.withoutTools(toolIds)
		}
		)
	}

	/**
	 * START OF DEPRECIATED API
	 */

	/*

	val staticTools: List<Tool> by lazy {
		tools.filter { it.identifyingData is Project.Id }
	}
	val primaryStack: ToolStack by lazy {
		windows.find { it.isPrimary }?.primaryStack!!
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



	fun withTemporaryToolMarkerOnPrimaryToolStack(marker: TempToolMarker): Layout
	{
		return Layout(
		  id,
		  projectId,
		  windows.map {
			  if (! it.isPrimary) it
			  else it.withTemporaryToolMarkerOnPrimaryToolStack(marker)
		  }
		)
	}

	fun addToolNextToMarker(tool: Tool<*>, marker: TempToolMarker): Layout
	{
		return Layout(
		  id,
		  projectId,
		  windows.map {
			  if (! it.hasMarker(marker)) it
			  else it.addToolNextToMarker(tool, marker)
		  }
		)
	}*/

	class Id(val uuid: UUID = UUID.randomUUID()) {
		override fun toString(): String = "Layout($uuid)"
	}

}