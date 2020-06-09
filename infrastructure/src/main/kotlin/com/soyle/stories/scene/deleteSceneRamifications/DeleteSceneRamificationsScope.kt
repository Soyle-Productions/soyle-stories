package com.soyle.stories.scene.deleteSceneRamifications

import com.soyle.stories.layout.config.temporary.DeleteSceneRamifications
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class DeleteSceneRamificationsScope(private val type: DeleteSceneRamifications, val projectScope: ProjectScope) : Scope() {

	val applicationScope
		get() = projectScope.applicationScope

	val sceneId
		get() = type.sceneId.toString()

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