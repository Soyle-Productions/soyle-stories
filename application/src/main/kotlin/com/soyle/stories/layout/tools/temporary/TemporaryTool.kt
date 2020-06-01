package com.soyle.stories.layout.tools.temporary

import com.soyle.stories.layout.tools.dynamic.DynamicTool

abstract class TemporaryTool : DynamicTool() {
	override val isTemporary: Boolean
		get() = true
}