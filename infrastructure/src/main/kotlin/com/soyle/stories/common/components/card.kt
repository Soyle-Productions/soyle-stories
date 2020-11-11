package com.soyle.stories.common.components

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.add
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.vbox

fun EventTarget.card(op: VBox.() -> Unit) = vbox {
    addClass(ComponentsStyles.card)
    op()
}

fun EventTarget.cardHeader(
    op: HBox.() -> Unit
) = hbox(spacing = 16, alignment = Pos.CENTER_LEFT) {
    addClass(ComponentsStyles.cardHeader)
    op()
}
fun EventTarget.cardBody(
    isFirstChild: Boolean = true,
    op: VBox.() -> Unit = {}
) = vbox {
    addClass(ComponentsStyles.cardBody)
    if (! isFirstChild) addClass(ComponentsStyles.notFirstChild)
    op()
}