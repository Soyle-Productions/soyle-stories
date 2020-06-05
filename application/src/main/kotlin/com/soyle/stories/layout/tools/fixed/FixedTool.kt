package com.soyle.stories.layout.tools.fixed

import com.soyle.stories.layout.tools.ToolType
import java.util.*

sealed class FixedTool : ToolType() {
	object CharacterList : FixedTool()
	object LocationList : FixedTool()
	object SceneList : FixedTool()
	object StoryEventList : FixedTool()


	override val isTemporary: Boolean
		get() = false

	override fun identifiedWithId(id: UUID): Boolean = false
}