package com.soyle.stories.project.layout

import com.soyle.stories.layout.usecases.*


internal fun toWindowViewModel(window: OpenWindow, locale: LayoutLocale) = WindowViewModel(window.id.toString(), toChildViewModel(window.child, locale))
internal fun toChildViewModel(windowChild: OpenWindowChild, locale: LayoutLocale): WindowChildViewModel = when (windowChild) {
	is OpenToolGroupSplitter -> GroupSplitterViewModel(
	  windowChild.id.toString(),
	  windowChild.orientation,
	  windowChild.children.map { it.first to toChildViewModel(it.second, locale) })
	is OpenToolGroup -> ToolGroupViewModel(windowChild.id.toString(), windowChild.focusedToolId?.toString(), windowChild.tools.map { it.toToolViewModel(locale) })
	else -> error("Unexpected window child $windowChild")
}

internal fun OpenTool.toToolViewModel(locale: LayoutLocale): ToolViewModel {
	return ToolViewModel(id.toString(), toolType, locale.toolName(toolType))
}