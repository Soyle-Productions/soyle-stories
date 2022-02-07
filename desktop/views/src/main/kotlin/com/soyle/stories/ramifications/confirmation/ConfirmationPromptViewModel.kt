package com.soyle.stories.ramifications.confirmation

import javafx.beans.property.BooleanProperty
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyObjectProperty
import tornadofx.booleanProperty
import tornadofx.getValue
import tornadofx.objectProperty
import tornadofx.setValue

open class ConfirmationPromptViewModel {

    protected val _isNeeded = booleanProperty(false)
    fun isNeeded(): ReadOnlyBooleanProperty = _isNeeded
    val isNeeded: Boolean by _isNeeded

    protected val _doNotShowAgain = booleanProperty(false)
    fun doNotShowAgain(): BooleanProperty = _doNotShowAgain
    var doNotShowAgain: Boolean by _doNotShowAgain

    protected var onConfirm: () -> Unit = {}
    open fun confirm() {
        onConfirm()
    }

    private val _onCheck = objectProperty<(() -> Unit)?>()
    fun onCheck(): ReadOnlyObjectProperty<(() -> Unit)?> = _onCheck
    var onCheck: (() -> Unit)? by _onCheck
        protected set

    open fun check() {
        onCheck?.invoke()
    }

    protected var onCancel: () -> Unit = {}
    open fun cancel() {
        onCancel()
    }

}