/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:35 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.di.DI
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.BaseStoryStructureToolViewModel
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.FX
import tornadofx.Scope
import tornadofx.removeFromParent

class BaseStoryStructureScope(val projectScope: ProjectScope, private val tool: BaseStoryStructureToolViewModel) : Scope() {

    private val isClosedProperty = SimpleBooleanProperty(false)
    private var isClosed
        get() = isClosedProperty.get()
        set(value) {
            if (isClosedProperty.value) return
            isClosedProperty.set(value)
        }

    val themeId: String
        get() = tool.themeId

    val characterId: String
        get() = tool.characterId

    init {
    	projectScope.addScope(tool.toolId, this)
    }

    fun close() {
        FX.getComponents(this).values.forEach {
            if (it is EventTarget) it.removeFromParent()
        }
        isClosed = true
        deregister()
        DI.deregister(this)
        projectScope.removeScope(tool.toolId, this)
    }

}