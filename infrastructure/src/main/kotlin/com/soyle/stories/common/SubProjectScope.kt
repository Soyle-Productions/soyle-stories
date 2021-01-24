package com.soyle.stories.common

import com.soyle.stories.di.DI
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

abstract class SubProjectScope(val projectScope: ProjectScope) : Scope() {

    private val isClosedProperty = ReadOnlyBooleanWrapper(this, "isClosed", false)
    fun isClosedProperty(): ReadOnlyBooleanProperty = isClosedProperty.readOnlyProperty
    var isClosed
        get() = isClosedProperty.get()
        private set(value) {
            if (isClosedProperty.value) return
            isClosedProperty.set(value)
        }

    open fun close() {
        FX.getComponents(this).values.forEach {
            if (it is EventTarget) it.removeFromParent()
        }
        DI.deregister(this)
        deregister()
        isClosed = true
    }

}