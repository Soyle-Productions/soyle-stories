package com.soyle.stories.layout.tools

abstract class TemporaryTool : DynamicTool() {
	override val isTemporary: Boolean
		get() = true
}