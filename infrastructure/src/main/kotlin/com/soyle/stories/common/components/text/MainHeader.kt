package com.soyle.stories.common.components.text

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import tornadofx.addClass
import tornadofx.label

fun EventTarget.mainHeader(text: String = "", graphic: Node? = null, op: Label.() -> Unit = {}) = label(text, graphic) {
    addClass(TextStyles.mainHeader)
    op()
}

fun EventTarget.mainHeader(
    observable: ObservableValue<String>,
    graphicProperty: ObservableValue<Node>? = null,
    op: Label.() -> Unit = {}
) = label(observable, graphicProperty) {
    addClass(TextStyles.mainHeader)
    op()
}