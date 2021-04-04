package com.soyle.stories.common.components.buttons

import com.soyle.stories.common.components.ComponentsStyles
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import tornadofx.addClass
import tornadofx.button

fun SecondaryButton(text: String = "", graphic: Node? = null, variant: ButtonVariant? = ButtonVariant.Filled) = Button(text, graphic).apply {
    addClass(ComponentsStyles.secondary)
    if (variant != null) addClass(variant.rule)
}

fun EventTarget.secondaryButton(text: String = "", graphic: Node? = null, variant: ButtonVariant? = ButtonVariant.Filled, op: Button.() -> Unit = {}) =
    button(text, graphic) {
        addClass(ComponentsStyles.secondary)
        if (variant != null) addClass(variant.rule)
        op()
    }

fun EventTarget.secondaryButton(text: ObservableValue<String>, graphic: Node? = null, variant: ButtonVariant? = ButtonVariant.Filled, op: Button.() -> Unit = {}) =
    button(text, graphic) {
        addClass(ComponentsStyles.secondary)
        if (variant != null) addClass(variant.rule)
        op()
    }