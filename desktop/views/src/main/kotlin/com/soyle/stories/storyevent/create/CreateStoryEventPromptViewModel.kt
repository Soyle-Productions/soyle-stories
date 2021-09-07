package com.soyle.stories.storyevent.create

import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import tornadofx.*
import kotlin.text.isNotBlank

class CreateStoryEventPromptViewModel(private val timeSpecified: Boolean) {

    val name = stringProperty()
    val timeText = stringProperty()
    val timeNotAlreadySpecified = ReadOnlyBooleanWrapper(!timeSpecified).readOnlyProperty
    val isCreating = booleanProperty()
    private val _isValid = ReadOnlyBooleanWrapper(false)
    val isValid get() = _isValid.readOnlyProperty
    val isCompleted = booleanProperty()

    init {
        name.onChange { _isValid.set(isValid()) }
        timeText.onChange { _isValid.set(isValid()) }
    }

    private fun isValid(): Boolean {
        if (name.value.isBlank()) return false
        if (! timeSpecified) {
            val timeText = timeText.value ?: return true
            if (timeText.isNotBlank() && timeText.toLongOrNull() == null) return false
        }
        return true
    }

}