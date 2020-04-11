/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:35 PM
 */
package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import tornadofx.*

class BaseStoryStructureScope(val projectScope: ProjectScope, val characterId: String, val themeId: String) : Scope() {

    private val isClosedProperty = SimpleBooleanProperty(false)
    private var isClosed
        get() = isClosedProperty.get()
        set(value) {
            if (isClosedProperty.value) return
            isClosedProperty.set(value)
        }

    fun close() {
        FX.getComponents(this).values.forEach {
            if (it is EventTarget) it.removeFromParent()
        }
        isClosed = true
        deregister()
    }

}