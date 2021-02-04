package com.soyle.stories.project.layout.config

import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.layout.tools.FixedTool

interface RegisteredToolsConfig {

	fun listFixedToolTypes(): List<FixedTool>

	fun getConfigFor(type: ToolType): ToolViewModelConfig

}