package com.soyle.stories.storyevent.time

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.StringProperty
import tornadofx.booleanBinding
import tornadofx.stringProperty

class TimeAdjustmentPromptViewModel(currentTime: Long? = null) {

    val time: StringProperty = stringProperty(currentTime?.toString() ?: "")
    private val timeShouldNotEqual = currentTime ?: 0L
    private val _submitting = ReadOnlyBooleanWrapper()
    val submitting: ReadOnlyBooleanProperty = _submitting.readOnlyProperty
    val canSubmit: BooleanBinding = time.booleanBinding {
        it?.toLongOrNull() != null && it.toLongOrNull() != timeShouldNotEqual
    }.and(submitting.not())
    private val _isCompleted = ReadOnlyBooleanWrapper()
    val isCompleted: ReadOnlyBooleanProperty = _isCompleted.readOnlyProperty

    fun submitting() {
        _submitting.set(true)
    }

    fun failed() {
        _submitting.set(false)
    }

    fun success() {
        _submitting.set(false)
        _isCompleted.set(true)
    }

}