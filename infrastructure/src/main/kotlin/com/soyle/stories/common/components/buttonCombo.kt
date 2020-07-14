package com.soyle.stories.common.components

import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.MenuButton
import tornadofx.addClass
import tornadofx.button
import tornadofx.menubutton

fun EventTarget.buttonCombo(text: String = "", graphic: Node? = null, op: MenuButton.() -> Unit = {}) = menubutton(text, graphic) {
    addClass(ComponentsStyles.buttonCombo)
    op()
}