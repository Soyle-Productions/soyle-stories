package com.soyle.stories.common.components

import javafx.beans.value.ObservableValue
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
fun EventTarget.buttonCombo(textProperty: ObservableValue<String>, graphic: Node? = null, op: MenuButton.() -> Unit = {}) = menubutton("", graphic) {
    addClass(ComponentsStyles.buttonCombo)
    textProperty().bind(textProperty)
    op()
}