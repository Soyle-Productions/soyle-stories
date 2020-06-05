package com.soyle.stories.project.layout

import com.soyle.stories.layout.tools.ToolType

interface LayoutLocale {
	fun toolName(toolType: ToolType): String
}