package com.soyle.stories.layout.tools

import java.util.*

abstract class FixedTool : ToolType() {

	override val isTemporary: Boolean
		get() = false

	override fun identifiedWithId(id: UUID): Boolean = false
}