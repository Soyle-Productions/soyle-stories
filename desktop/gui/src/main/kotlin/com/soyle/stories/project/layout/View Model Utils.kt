package com.soyle.stories.project.layout

import com.soyle.stories.layout.usecases.*
import com.soyle.stories.project.layout.config.RegisteredToolsConfig


internal fun toWindowViewModel(window: OpenWindow, config: RegisteredToolsConfig) = WindowViewModel(window.id.toString(), toChildViewModel(window.child, config))
internal fun toChildViewModel(windowChild: OpenWindowChild, config: RegisteredToolsConfig): WindowChildViewModel = when (windowChild) {
	is OpenToolGroupSplitter -> GroupSplitterViewModel(
	  windowChild.id.toString(),
	  windowChild.orientation,
	  windowChild.children.map { it.first to toChildViewModel(it.second, config) })
	is OpenToolGroup -> ToolGroupViewModel(windowChild.id.toString(), windowChild.focusedToolId?.toString(), windowChild.tools.map { it.toToolViewModel(config) })
	else -> error("Unexpected window child $windowChild")
}

internal fun OpenTool.toToolViewModel(config: RegisteredToolsConfig): ToolViewModel {
	return ToolViewModel(id.toString(), toolType, config.getConfigFor(toolType).toolName())
}

/*
fun OpenTool.toToolViewModel(locale: LayoutLocale): ToolViewModel
{
	return ToolViewModel(id.toString(), toolType, toolConfig.getConfigFor(toolType).toolName(locale))
}
 */