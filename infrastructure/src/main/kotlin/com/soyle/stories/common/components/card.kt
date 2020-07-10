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

inline fun EventTarget.card(crossinline op: VBox.() -> Unit) = vbox {
    addClass(ComponentsStyles.card)
    op()
}

inline fun EventTarget.cardHeader(
    crossinline op: HBox.() -> Unit
) = hbox(spacing = 16, alignment = Pos.CENTER_LEFT) {
    addClass(ComponentsStyles.cardHeader)
    op()
}
inline fun EventTarget.cardBody(
    crossinline op: VBox.() -> Unit
) = vbox {
    addClass(ComponentsStyles.cardBody)
    op()
}