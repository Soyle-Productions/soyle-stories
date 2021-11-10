package com.soyle.stories.storyevent.time

import javafx.beans.binding.BooleanExpression
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.StringProperty
import tornadofx.*

abstract class StoryEventTimeChangeViewModel {

    private val timeTextProperty = stringProperty("")
    fun timeText(): StringProperty = timeTextProperty
    var time: Long?
        get() {
            val adjustmentText = timeTextProperty.get()
            if (adjustmentText.isEmpty()) return 0
            else return adjustmentText.toLongOrNull()
        }
        set(value) {
            timeTextProperty.set(value.toString())
        }

    protected abstract val canSubmitExpression: BooleanExpression
    fun canSubmit(): BooleanExpression = canSubmitExpression
    val canSubmit: Boolean
        get() = canSubmitExpression.get()

    private val submittingExpression = ReadOnlyBooleanWrapper(false)
    fun submitting(): BooleanExpression = submittingExpression.readOnlyProperty
    val isSubmitting: Boolean
        get() = submittingExpression.get()

    private val onSubmitProperty = objectProperty<() -> Unit> {}
    fun setOnSubmit(block: () -> Unit) {
        onSubmitProperty.set(block)
    }

    fun submit() {
        if (! canSubmit) return
        submittingExpression.set(true)
        onSubmitProperty.get().invoke()
    }

    fun endSubmission() {
        submittingExpression.set(false)
    }

}