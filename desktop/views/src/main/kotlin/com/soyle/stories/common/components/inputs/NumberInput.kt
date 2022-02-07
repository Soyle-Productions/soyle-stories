package com.soyle.stories.common.components.inputs

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.common.scopedListener
import javafx.beans.InvalidationListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.input.KeyEvent
import tornadofx.*

const val ValidIntChars = "-+0123456789"
const val ValidFloatChars = "-+0123456789."

typealias ConfigureNode = @ViewBuilder Node.() -> Unit

@Suppress("NOTHING_TO_INLINE")
inline fun Node.onAction(noinline handler: (ActionEvent) -> Unit) {
    addEventHandler(ActionEvent.ACTION, handler)
}

/**
 * Only allows numerical characters to be entered
 */
@ViewBuilder
fun EventTarget.numberField(
    validChars: String = ValidFloatChars,
    hint: String = "0",
    value: ObservableValue<String> = stringProperty(),
    onValueChange: (String) -> Unit = {},
    configure: ConfigureNode = {}
): Node = textfield {
    addClass(InputStyles.numberField)
    promptText = hint
    scopedListener(value) { text = it }
    textProperty().addListener(InvalidationListener { onValueChange(text) })
    addEventFilter(KeyEvent.KEY_TYPED) {
        if (it.character !in validChars) it.consume()
    }
    configure()
}

@ViewBuilder
fun <T : Number?> EventTarget.numberInput(
    defaultValue: T,
    validChars: String,
    toNumber: (String?) -> T?,
    toString: (Number?) -> String,
    hint: String = "0",
    value: ObservableValue<Number> = objectProperty(),
    onValueChange: (T) -> Unit = {},
    configure: ConfigureNode = {}
): Node {
    fun notifyValueChange(newValue: T) {
        if (newValue != value.value) onValueChange(newValue)
    }

    val text = stringProperty("").apply {
        scopedListener(value) { set(toString(it)) }
        onChange {
            val possibleNewValue = toNumber(it)
            if (possibleNewValue != null) notifyValueChange(possibleNewValue)
        }
    }

    return numberField(
        hint,
        validChars,
        value = text,
        onValueChange = text::set,
    ) {
        onLoseFocus { notifyValueChange(toNumber(text.value) ?: defaultValue) }
        onAction { notifyValueChange(toNumber(text.value) ?: defaultValue) }
        configure()
    }
}

@ViewBuilder
fun <T : Number> EventTarget.optionalNumberInput(
    validChars: String,
    toNumber: (String?) -> T?,
    toString: (T?) -> String,
    hint: String = "0",
    value: ObservableValue<T?> = objectProperty(),
    onValueChange: (T?) -> Unit = {},
    configure: ConfigureNode = {}
): Node {
    fun notifyValueChange(newValue: T?) {
        if (newValue != value.value) onValueChange(newValue)
    }

    val text = stringProperty("").apply {
        scopedListener(value) { set(toString(it)) }
        onChange {
            val possibleNewValue = toNumber(it)
            if (possibleNewValue != null) notifyValueChange(possibleNewValue)
        }
    }

    return numberField(
        hint,
        validChars,
        value = text,
        onValueChange = text::set
    ) {
        onLoseFocus { notifyValueChange(toNumber(text.value)) }
        onAction { notifyValueChange(toNumber(text.value)) }
        configure()
    }
}

@ViewBuilder
fun EventTarget.intInput(
    defaultValue: Int = 0,
    validChars: String = ValidIntChars,
    toNumber: (String?) -> Int? = { it?.toIntOrNull() },
    toString: (Number?) -> String = { it?.toString().orEmpty() },
    hint: String = "0",
    value: ObservableValue<Number> = intProperty(),
    onValueChange: (Int) -> Unit = {},
    configure: ConfigureNode = {}
): Node = numberInput<Int>(
    defaultValue,
    validChars,
    toNumber,
    toString,
    hint,
    value,
    onValueChange,
    configure
)

@ViewBuilder
fun EventTarget.optionalIntInput(
    validChars: String = ValidIntChars,
    toNumber: (String?) -> Int? = { it?.toIntOrNull() },
    toString: (Int?) -> String = { it?.toString().orEmpty() },
    hint: String = "0",
    value: ObservableValue<Int?> = objectProperty(),
    onValueChange: (Int?) -> Unit = {},
    configure: ConfigureNode = {}
): Node = optionalNumberInput<Int>(
    validChars,
    toNumber,
    toString,
    hint,
    value,
    onValueChange,
    configure
)

@ViewBuilder
fun EventTarget.longInput(
    defaultValue: Long = 0,
    validChars: String = ValidIntChars,
    toNumber: (String?) -> Long? = { it?.toLongOrNull() },
    toString: (Number?) -> String = { it?.toString().orEmpty() },
    hint: String = "0",
    value: ObservableValue<Number> = longProperty(defaultValue),
    onValueChange: (Long) -> Unit = {},
    configure: ConfigureNode = {}
): Node = numberInput<Long>(
    defaultValue,
    validChars,
    toNumber,
    toString,
    hint,
    value,
    onValueChange,
    configure
)

@ViewBuilder
fun EventTarget.optionalLongInput(
    validChars: String = ValidIntChars,
    toNumber: (String?) -> Long? = { it?.toLongOrNull() },
    toString: (Long?) -> String = { it?.toString().orEmpty() },
    hint: String = "0",
    value: ObservableValue<Long?> = objectProperty(),
    onValueChange: (Long?) -> Unit = {},
    configure: ConfigureNode = {}
): Node = optionalNumberInput<Long>(
    validChars,
    toNumber,
    toString,
    hint,
    value,
    onValueChange,
    configure
)

@ViewBuilder
fun EventTarget.doubleInput(
    defaultValue: Double = 0.0,
    validChars: String = ValidFloatChars,
    toNumber: (String?) -> Double? = { it?.toDoubleOrNull() },
    toString: (Number?) -> String = { it?.toString().orEmpty() },
    hint: String = "0.0",
    value: ObservableValue<Number> = doubleProperty(),
    onValueChange: (Double) -> Unit = {},
    configure: ConfigureNode = {}
): Node = numberInput<Double>(
    defaultValue,
    validChars,
    toNumber,
    toString,
    hint,
    value,
    onValueChange,
    configure
)

@ViewBuilder
fun EventTarget.floatInput(
    defaultValue: Float = 0f,
    validChars: String = ValidFloatChars,
    toNumber: (String?) -> Float? = { it?.toFloatOrNull() },
    toString: (Number?) -> String = { it?.toString().orEmpty() },
    hint: String = "0.0",
    value: ObservableValue<Number> = floatProperty(),
    onValueChange: (Float) -> Unit = {},
    configure: ConfigureNode = {}
): Node = numberInput<Float>(
    defaultValue,
    validChars,
    toNumber,
    toString,
    hint,
    value,
    onValueChange,
    configure
)