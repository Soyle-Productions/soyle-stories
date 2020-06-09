package com.soyle.stories.common

import com.soyle.stories.di.DI
import com.soyle.stories.layout.tools.ToolType
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

abstract class ToolScope<T : ToolType>(val projectScope: ProjectScope, val toolId: String, val type: T) : Scope() {

	private val isClosedProperty = SimpleBooleanProperty(false)
	private var isClosed
		get() = isClosedProperty.get()
		set(value) {
			if (isClosedProperty.value) return
			isClosedProperty.set(value)
		}

	init {
		projectScope.addScope(toolId, this)
	}

	fun close() {
		FX.getComponents(this).values.forEach {
			if (it is EventTarget) it.removeFromParent()
		}
		isClosed = true
		deregister()
		DI.deregister(this)
		projectScope.removeScope(toolId, this)
	}
}