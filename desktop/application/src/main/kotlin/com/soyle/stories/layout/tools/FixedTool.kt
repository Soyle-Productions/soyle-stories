package com.soyle.stories.layout.tools

import com.soyle.stories.layout.repositories.OpenToolContext
import java.util.*

abstract class FixedTool : DynamicTool() {

	override val isTemporary: Boolean
		get() = false

	override fun identifiedWithId(id: UUID): Boolean = false
	override suspend fun validate(context: OpenToolContext) {}
}