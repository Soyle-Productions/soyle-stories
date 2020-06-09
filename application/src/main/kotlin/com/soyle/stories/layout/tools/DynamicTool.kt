package com.soyle.stories.layout.tools

import com.soyle.stories.layout.repositories.OpenToolContext

abstract class DynamicTool : ToolType() {

	override val isTemporary: Boolean
		get() = false

	abstract suspend fun validate(context: OpenToolContext)

}