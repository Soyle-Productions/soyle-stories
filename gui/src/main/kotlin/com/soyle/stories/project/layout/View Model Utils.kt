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

internal fun WindowViewModel.updateGroup(group: OpenToolGroup): WindowViewModel {
	return WindowViewModel(id, child.updateGroup(group))
}

internal fun WindowChildViewModel.updateGroup(group: OpenToolGroup): WindowChildViewModel = when (this) {
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

internal fun toWindowViewModel(window: OpenWindow) = WindowViewModel(window.windowId.toString(), toChildViewModel(window.child))
internal fun toChildViewModel(windowChild: OpenWindowChild): WindowChildViewModel = when (windowChild) {
	is OpenToolGroupSplitter -> GroupSplitterViewModel(
	  windowChild.splitterId.toString(),
	  windowChild.orientation,
	  windowChild.children.map { it.first to toChildViewModel(it.second) })
	is OpenToolGroup -> ToolGroupViewModel(windowChild.groupId.toString(), windowChild.focusedToolId?.toString(), windowChild.tools.map { it.toToolViewModel() })
	else -> error("Unexpected window child $windowChild")
}

internal fun OpenTool.toToolViewModel(): ToolViewModel = when (this) {
	is CharacterListTool -> CharacterListToolViewModel(toolId.toString())
	is LocationListTool -> LocationListToolViewModel(toolId.toString())
	is SceneListTool -> SceneListToolViewModel(toolId.toString())
	is StoryEventListTool -> StoryEventListToolViewModel(toolId.toString())
	is BaseStoryStructureTool -> BaseStoryStructureToolViewModel(toolId.toString(), characterId.toString(), themeId.toString())
	is CharacterComparisonTool -> CharacterComparisonToolViewModel(toolId.toString(), themeId.toString(), characterId.toString())
	is LocationDetailsTool -> LocationDetailsToolViewModel(toolId.toString(), locationId.toString())
}