package com.soyle.stories.common

import com.soyle.stories.di.DI
import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

abstract class ToolScope<T : ToolType>(projectScope: ProjectScope, val toolId: String, val type: T) : SubProjectScope(projectScope) {

	init {
		projectScope.addScope(toolId, this)
	}

	override fun close() {
		super.close()
		projectScope.removeScope(toolId, this)
	}
}