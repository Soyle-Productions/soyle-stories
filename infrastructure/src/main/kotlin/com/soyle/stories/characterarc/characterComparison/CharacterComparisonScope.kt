/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 11:10 PM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.project.ProjectScope
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.EventTarget
import kotlinx.coroutines.runBlocking
import tornadofx.*

class CharacterComparisonScope(val projectScope: ProjectScope, val themeId: String, characterId: String) : Scope() {

    private val model = find<CharacterComparisonModel>(scope = this)
    private val characterComparisonViewListener = find<CharacterComparisonComponent>(scope = this).characterComparisonViewListener

    private val isClosedProperty = SimpleBooleanProperty(false)
    private var isClosed
        get() = isClosedProperty.get()
        set(value) {
            if (isClosedProperty.value) return
            isClosedProperty.set(value)
        }

    init {
        getCharacterComparison(characterId)

        model.isInvalid.onChangeUntil(isClosedProperty) {
            if (it == true) {
                getCharacterComparison(model.focusedCharacter.value.characterId)
            }
        }
    }

    private fun getCharacterComparison(characterId: String) {
        runAsync {
            runBlocking {
                characterComparisonViewListener.getCharacterComparison(characterId)
            }
        }
    }

    fun close() {
        FX.getComponents(this).values.forEach {
            if (it is EventTarget) it.removeFromParent()
        }
        isClosed = true
        deregister()
    }

}