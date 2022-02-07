package com.soyle.stories.common.components.inputs

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.scopedListener
import com.soyle.stories.domain.validation.NonBlankString
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.Parent
import tornadofx.*

/**
 * @param onAction Called when the user presses ENTER while focused on the [Node]
 */
@ViewBuilder
fun EventTarget.nonBlankTextField(
    value: NonBlankString?,
    onValueChange: (NonBlankString?) -> Unit = {},
    configure: @ViewBuilder Node.() -> Unit = {}
): Node = nonBlankTextField(
    value = objectProperty(value),
    onValueChange,
    configure
)

/**
 * @param onAction Called when the user presses ENTER while focused on the [Node]
 */
@ViewBuilder
fun EventTarget.nonBlankTextField(
    value: ObservableValue<NonBlankString?> = objectProperty(),
    onValueChange: (NonBlankString?) -> Unit = {},
    configure: @ViewBuilder Node.() -> Unit = {}
): Node = textfield {
    scopedListener(value) { text = it?.value.orEmpty() }
    addClass(InputStyles.nonBlankTextField)
    var blankDecorator: Decorator? = null
    textProperty().onChange {
        val newValue = NonBlankString.create(it.orEmpty())
        if (newValue == null) {
            if (blankDecorator == null) {
                blankDecorator = SimpleMessageDecorator(
                    "Name cannot be blank",
                    ValidationSeverity.Error
                ).also { addDecorator(it) }
            }
        } else {
            blankDecorator?.let { removeDecorator(it) }
            blankDecorator = null
        }
        onValueChange(newValue)
    }
    configure()
}