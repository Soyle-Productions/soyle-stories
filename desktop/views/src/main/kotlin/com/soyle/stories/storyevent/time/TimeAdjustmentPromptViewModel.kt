package com.soyle.stories.storyevent.time

import javafx.beans.binding.BooleanBinding
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.StringProperty
import tornadofx.booleanBinding
import tornadofx.stringProperty

class TimeAdjustmentPromptViewModel private constructor(
    val time: StringProperty,
    private val timeShouldNotEqual: Long,
    val adjustment: Boolean
) {

    companion object {
        fun adjustment(initialValue: Long = 0L) = TimeAdjustmentPromptViewModel(
            stringProperty(initialValue.toString()),
            0,
            true
        )
        fun reschedule(currentTime: Long) = TimeAdjustmentPromptViewModel(
            stringProperty(currentTime.toString()),
            currentTime,
            false
        )
    }

    private val _submitting = ReadOnlyBooleanWrapper()
    val submitting: ReadOnlyBooleanProperty = _submitting.readOnlyProperty
    val canSubmit: BooleanBinding = booleanBinding(time) {
        val timeAsLong = time.get().toLongOrNull() ?: return@booleanBinding false
        timeAsLong != timeShouldNotEqual
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