package com.soyle.stories.theme.themeOppositionWebs

import com.soyle.stories.layout.config.dynamic.ValueOppositionWebs
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent
import java.util.*

class ValueOppositionWebsScope(val projectScope: ProjectScope, private val toolId: String, type: ValueOppositionWebs) : Scope() {

    val themeId: UUID = type.themeId

    init {
        projectScope.addScope(toolId, this)
    }

    fun close() {
        FX.getComponents(this).values.forEach {
            if (it is EventTarget) it.removeFromParent()
        }
        projectScope.removeScope(toolId, this)
        deregister()
    }

}