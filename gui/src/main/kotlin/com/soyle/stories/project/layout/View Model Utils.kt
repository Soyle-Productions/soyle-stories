package com.soyle.stories.project.layout

import com.soyle.stories.layout.usecases.*
import java.util.*

internal fun WindowViewModel.containsGroup(groupId: UUID): Boolean {
	return child.containsGroup(groupId)
}

internal fun WindowChildViewModel.containsGroup(groupId: UUID): Boolean {
	return when (this) {
		is GroupSplitterViewModel -> children.find { it.second.containsGroup(groupId) } != null
		is ToolGroupViewModel -> id == groupId.toString()
	}
}

internal fun WindowViewModel.updateGroup(group: ActiveToolGroup): WindowViewModel {
	return WindowViewModel(id, child.updateGroup(group))
}

internal fun WindowChildViewModel.updateGroup(group: ActiveToolGroup): WindowChildViewModel = when (this) {
	is GroupSplitterViewModel -> if (!containsGroup(group.groupId)) this else {
		GroupSplitterViewModel(splitterId, orientation, children.map {
			it.copy(second = it.second.updateGroup(group))
		})
	}
	is ToolGroupViewModel -> if (!containsGroup(group.groupId)) this else {
		ToolGroupViewModel(groupId, group.focusedToolId?.toString(), group.tools.map {
			it.toToolViewModel()
		})
	}
}

internal fun toWindowViewModel(window: ActiveWindow) = WindowViewModel(window.windowId.toString(), toChildViewModel(window.child))
internal fun toChildViewModel(windowChild: ActiveWindowChild): WindowChildViewModel = when (windowChild) {
	is ActiveToolGroupSplitter -> GroupSplitterViewModel(
	  windowChild.splitterId.toString(),
	  windowChild.orientation,
	  windowChild.children.map { it.first to toChildViewModel(it.second) })
	is ActiveToolGroup -> ToolGroupViewModel(windowChild.groupId.toString(), windowChild.focusedToolId?.toString(), windowChild.tools.map { it.toToolViewModel() })
	else -> error("Unexpected window child $windowChild")
}

internal fun ActiveTool.toToolViewModel(): ToolViewModel = when (this) {
	is CharacterListActiveTool -> CharacterListToolViewModel(toolId.toString())
	is LocationListActiveTool -> LocationListToolViewModel(toolId.toString())
	is BaseStoryStructureActiveTool -> BaseStoryStructureToolViewModel(toolId.toString(), characterId.toString(), themeId.toString())
	is CharacterComparisonActiveTool -> CharacterComparisonToolViewModel(toolId.toString(), characterId.toString(), themeId.toString())
	is LocationDetailsActiveTool -> LocationDetailsToolViewModel(toolId.toString(), locationId.toString())
}