package com.soyle.stories.common.components.buttons

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Button
import tornadofx.addClass


fun EventTarget.inviteButton(text: String = "", graphic: Node? = null, op: Button.() -> Unit = {}) =
    primaryButton(text, graphic) {
        addClass(Styles.inviteButton)
        op()
    }

fun EventTarget.inviteButton(text: ObservableValue<String>, graphic: Node? = null, op: Button.() -> Unit = {}) =
    primaryButton(text, graphic) {
        addClass(Styles.inviteButton)
        op()
    }