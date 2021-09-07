package com.soyle.stories.storyevent.rename

import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import tornadofx.isNotBlank
import tornadofx.and

class RenameStoryEventPromptViewModel(private val currentName: String) {

    private val _name = ReadOnlyStringWrapper(currentName)

    /**
     * The current value of the name.  Starts as being equal to [currentName] and can be updated via [nameProperty]
     */
    val name: ReadOnlyStringProperty get() = _name.readOnlyProperty

    /**
     * A writeable interface for the [name] property
     */
    fun nameProperty(): Property<String> = _name

    private val _isValid = ReadOnlyBooleanWrapper()

    /**
     * Only ever `true` if [name] is not equal to [currentName] and [name] is not blank
     */
    val isValid: ReadOnlyBooleanProperty get() = _isValid.readOnlyProperty
    init {
        _isValid.bind(name.isNotEqualTo(currentName) and name.isNotBlank())
    }

    private val _disabled = ReadOnlyBooleanWrapper()

    /**
     * Can be toggled by calling [disable] or [enable]
     */
    val isDisabled: ReadOnlyBooleanProperty get() = _disabled.readOnlyProperty
    val isEnabled = isDisabled.not()

    /**
     * sets [isDisabled] to `true` if [isValid] is already `true`
     */
    fun disable() {
        if (_isValid.value) _disabled.set(true)
    }

    /**
     * sets [isDisabled] to `false`
     */
    fun enable() {
        _disabled.set(false)
    }

    private val _isCompleted = ReadOnlyBooleanWrapper()

    /**
     * Can be set to `true` exactly once by a call to [cancel] or [complete]
     */
    val isCompleted get() = _isCompleted.readOnlyProperty

    /**
     * sets the [isCompleted] property to `true` no matter what
     */
    fun cancel() {
        _isCompleted.set(true)
    }

    /**
     * sets the [isCompleted] property to `true` if [isValid] is already `true`
     */
    fun complete() {
        if (_isValid.value) _isCompleted.set(true)
    }

}