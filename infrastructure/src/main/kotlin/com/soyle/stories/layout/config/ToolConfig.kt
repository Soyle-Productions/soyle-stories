package com.soyle.stories.layout.config

import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.project.layout.ToolViewModel
import com.soyle.stories.project.layout.config.ToolViewModelConfig
import kotlin.reflect.KClass

interface ToolConfig<T : ToolType> {

	fun getViewModelConfig(type: T): ToolViewModelConfig
	fun getTabConfig(tool: ToolViewModel, type: T): ToolTabConfig
	fun getFixedType(): FixedTool?
	fun getRegistration(): Pair<KClass<T>, ToolConfig<T>>

}