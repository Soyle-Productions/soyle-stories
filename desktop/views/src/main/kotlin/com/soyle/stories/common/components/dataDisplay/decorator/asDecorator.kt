package com.soyle.stories.common.components.dataDisplay.decorator

import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.ObjectExpression
import javafx.beans.property.ObjectProperty
import javafx.beans.value.ObservableValue
import tornadofx.Decorator
import tornadofx.SimpleMessageDecorator
import tornadofx.ValidationSeverity
import tornadofx.objectBinding

fun ObservableValue<String?>.asDecorator(severity: ValidationSeverity = ValidationSeverity.Error): ObjectExpression<Decorator?> {
    return createObjectBinding<Decorator?>({
        if (value == null) null
        SimpleMessageDecorator(value, severity)
    }, this)
}