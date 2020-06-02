package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.layout.tools.temporary.Ramifications
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class RamificationsScope(val projectScope: ProjectScope, val type: Ramifications) : Scope() {

	private val isClosedProperty = SimpleBooleanProperty(false)
	private var isClosed
		get() = isClosedProperty.get()
		set(value) {
			if (isClosedProperty.value) return
			isClosedProperty.set(value)
		}

	init {
		projectScope.addScope(type.toString(), this)
	}

	fun close() {
		FX.getComponents(this).values.forEach {
			if (it is EventTarget) it.removeFromParent()
		}
		isClosed = true
		deregister()
	}

}