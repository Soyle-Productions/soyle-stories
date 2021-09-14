package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.ThreadTransformer
import javafx.beans.binding.BooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.BooleanProperty
import javafx.beans.property.ObjectProperty
import tornadofx.*

class RemoveStoryEventConfirmationPromptViewModel(
    private val threadTransformer: ThreadTransformer
) {

    private enum class Invariant(
        val isShowing: Boolean,
        val isConfirming: Boolean,
        val canConfirm: Boolean,
        val isCompleted: Boolean
    ) {

        Undefined(false, false, false, false),
        Unneeded(false, true, false, false),
        AwaitingConfirmation(true, false, true, false),
        Confirming(true, true, false, false),
        Confirmed(false, false, false, true)

    }

    private val invariantProperty: ObjectProperty<Invariant> = objectProperty<Invariant>(Invariant.Undefined)
    private var invariant: Invariant
        get() = invariantProperty.value
        set(value) {
            if (threadTransformer.isGuiThread()) invariantProperty.set(value)
            else threadTransformer.gui { invariantProperty.set(value) }
        }

    private val showingProperty: BooleanBinding = invariantProperty.booleanBinding { it!!.isShowing }
    fun showing(): BooleanExpression = showingProperty
    val isShowing: Boolean by showingProperty

    private val completedProperty: BooleanBinding = invariantProperty.booleanBinding { it!!.isCompleted }
    fun completed(): BooleanExpression = completedProperty
    val isCompleted: Boolean by completedProperty

    val isConfirming: Boolean get() = invariant.isConfirming

    private val canConfirmProperty: BooleanBinding = invariantProperty.booleanBinding { it!!.canConfirm }
    fun canConfirm(): BooleanExpression = canConfirmProperty
    val canConfirm: Boolean by canConfirmProperty

    private val shouldNotShowAgainProperty: BooleanProperty = booleanProperty()
    fun shouldNotShowAgain() = shouldNotShowAgainProperty
    var shouldNotShowAgain: Boolean by shouldNotShowAgainProperty

    fun unneeded() {
        invariant = Invariant.Unneeded
        shouldNotShowAgain = true
    }

    fun needed() {
        invariant = Invariant.AwaitingConfirmation
    }

    fun cancel() {
        if (!isShowing) return
        invariant = Invariant.Confirmed
    }

    fun confirm() {
        if (!isShowing) return
        invariant = Invariant.Confirming
    }

    fun failed() {
        invariant = Invariant.AwaitingConfirmation
    }

    fun complete() {
        if (!isConfirming) return
        invariant = Invariant.Confirmed
    }

}