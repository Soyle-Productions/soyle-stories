package com.soyle.stories.character.create

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.scopedListener
import com.soyle.stories.domain.validation.NonBlankString
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.*

@ViewBuilder
fun EventTarget.characterNameInput(
    initialValue: String = "",
    onValid: (NonBlankString) -> Unit = {},
): Parent = characterNameInput(initialValue.toProperty(), onValid)

@ViewBuilder
fun EventTarget.characterNameInput(
    initialValue: ObservableValue<String>,
    onValid: (NonBlankString) -> Unit = {},
): Parent = textfield {
    scopedListener(initialValue) { text = it }
    addClass("character-name-input")
    textProperty().onChange {
        if (decorators.isNotEmpty()) decorators.toList().forEach { removeDecorator(it) }
    }
    action {
        val name = NonBlankString.create(text)
        if (name == null) {
            val errorDecorator = SimpleMessageDecorator("Name cannot be blank", ValidationSeverity.Error)
            decorators.toList().forEach { removeDecorator(it) }
            addDecorator(errorDecorator)
        } else {
            onValid(name)
        }
    }
}