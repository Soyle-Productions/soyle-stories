package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.di.DI
import com.soyle.stories.layout.tools.dynamic.BaseStoryStructure
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class BaseStoryStructureScope(val projectScope: ProjectScope, private val toolId: String, private val tool: BaseStoryStructure) : Scope() {

    private val isClosedProperty = SimpleBooleanProperty(false)
    private var isClosed
        get() = isClosedProperty.get()
        set(value) {
            if (isClosedProperty.value) return
            isClosedProperty.set(value)
        }

    val themeId: String
        get() = tool.themeId.toString()

    val characterId: String
        get() = tool.characterId.toString()

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