package com.soyle.stories.storyevent.create

import com.soyle.stories.domain.validation.NonBlankString
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.*
import javafx.event.ActionEvent
import javafx.event.EventHandler
import tornadofx.*

class CreateStoryEventPrompt {

    private val submittingExpression = ReadOnlyBooleanWrapper(false)
    fun submitting(): BooleanExpression = submittingExpression.readOnlyProperty

    private val timeFieldShownExpression = booleanProperty(true)
    fun timeFieldShown(): BooleanProperty = timeFieldShownExpression
    var isTimeFieldShown: Boolean by timeFieldShownExpression

    private val nameProperty = stringProperty()
    fun name(): StringProperty = nameProperty
    val name: NonBlankString?
        get() = NonBlankString.create(nameProperty.get())

    private val timeTextProperty = stringProperty()
    fun timeText(): StringProperty = timeTextProperty
    val time: Long?
        get() {
            val timeText = timeTextProperty.get()
            return if (timeText.isNotBlank()) timeText.toLongOrNull()
            else 0
        }

    private val canSubmitExpression = createBooleanBinding({
        name != null && (! isTimeFieldShown || time != null)
    }, nameProperty, timeTextProperty, timeFieldShownExpression)
    fun canSubmit(): BooleanExpression = canSubmitExpression
    val canSubmit: Boolean by canSubmit()

    private val onSubmitProperty = objectProperty<() -> Unit> { }
    var onSubmit: () -> Unit by onSubmitProperty

    @JvmName("onSubmit")
    fun setOnSubmit(handler: () -> Unit) {
        onSubmit = handler
    }

    fun submit() {
        if (! canSubmit) return
        onSubmit.invoke()
    }

}