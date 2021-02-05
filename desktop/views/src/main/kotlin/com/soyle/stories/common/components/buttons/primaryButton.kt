package com.soyle.stories.common.components.buttons

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import tornadofx.addClass
import tornadofx.button

fun EventTarget.primaryButton(text: String = "", graphic: Node? = null, op: Button.() -> Unit = {}) =
    button(text, graphic) {
        addClass(ButtonStyles.primaryButton)
        op()
    }

fun EventTarget.primaryButton(text: ObservableValue<String>, graphic: Node? = null, op: Button.() -> Unit = {}) =
    button(text, graphic) {
        addClass(ButtonStyles.primaryButton)
        op()
    }