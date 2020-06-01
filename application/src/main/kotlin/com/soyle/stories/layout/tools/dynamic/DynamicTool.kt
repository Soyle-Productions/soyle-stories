package com.soyle.stories.layout.tools.dynamic

import com.soyle.stories.layout.repositories.OpenToolContext
import com.soyle.stories.layout.tools.ToolType

abstract class DynamicTool : ToolType() {

	abstract suspend fun validate(context: OpenToolContext)

}