package com.soyle.stories.scene.reorderSceneRamifications

import com.soyle.stories.layout.config.temporary.DeleteSceneRamifications
import com.soyle.stories.layout.config.temporary.ReorderSceneRamifications
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class ReorderSceneRamificationsScope(val toolId: String, private val type: ReorderSceneRamifications, val projectScope: ProjectScope) : Scope() {

	val applicationScope
		get() = projectScope.applicationScope

	val sceneId
		get() = type.sceneId.toString()

	val reorderIndex
		get() = type.newIndex

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
		projectScope.removeScope(toolId, this)
		deregister()
	}
}