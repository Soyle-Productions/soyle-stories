package com.soyle.stories.layout.tools

import java.util.*

abstract class ToolType {
	abstract val isTemporary: Boolean

	abstract fun identifiedWithId(id: UUID): Boolean
}