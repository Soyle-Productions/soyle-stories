package com.soyle.stories.scene.create

import com.soyle.stories.domain.validation.NonBlankString
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.ObjectExpression
import javafx.beans.property.StringProperty
import tornadofx.*

class CreateScenePromptViewModel {

    private val nameProperty = stringProperty()
    fun name(): StringProperty = nameProperty
    var name: String by nameProperty

    private val errorMessageProperty = objectProperty<String?>(null)
    fun errorMessage(): ObjectExpression<String?> = errorMessageProperty

    private val submittingProperty = booleanProperty(false)
    fun submitting(): BooleanExpression = submittingProperty
    val submitting: Boolean by submittingProperty

    fun canSubmit(): BooleanExpression = nameProperty.booleanBinding(submittingProperty) {
        it != null && NonBlankString.create(it) != null && !submitting
    }

    private var onSubmit: (NonBlankString) -> Unit = {}

    fun submit() {
        val validName = NonBlankString.create(name)
        if (validName == null) {
            errorMessageProperty.set("Name cannot be blank")
            return
        }
        errorMessageProperty.set(null)
        submittingProperty.set(true)
        onSubmit(validName)
    }

    fun setOnSubmit(handler: (NonBlankString) -> Unit) {
        onSubmit = handler
    }

    fun reset() {
        nameProperty.set("")
        errorMessageProperty.set(null)
        submittingProperty.set(false)
    }

}