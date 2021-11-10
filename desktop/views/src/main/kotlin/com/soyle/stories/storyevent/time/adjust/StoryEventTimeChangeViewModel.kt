package com.soyle.stories.storyevent.time.adjust

import javafx.beans.binding.BooleanExpression
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.StringProperty
import tornadofx.booleanBinding
import tornadofx.stringProperty
import tornadofx.getValue
import tornadofx.objectProperty

class StoryEventTimeChangeViewModel {

    private val adjustmentProperty = stringProperty()
    fun adjustment(): StringProperty = adjustmentProperty
    var adjustment: Long?
        get() {
            val adjustmentText = adjustmentProperty.get()
            if (adjustmentText.isBlank()) return 0
            else return adjustmentText.toLongOrNull()
        }
        set(value) {
            adjustmentProperty.set(value.toString())
        }

    private val canSubmitExpression = booleanBinding(adjustmentProperty) { adjustment != null && adjustment != 0L }
    fun canSubmit(): BooleanExpression = canSubmitExpression
    val canSubmit: Boolean by canSubmit()

    private val submittingExpression = ReadOnlyBooleanWrapper(false)
    fun submitting(): BooleanExpression = submittingExpression.readOnlyProperty

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