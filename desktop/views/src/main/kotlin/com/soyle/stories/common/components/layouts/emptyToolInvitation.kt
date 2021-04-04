package com.soyle.stories.common.components.layouts

import com.soyle.stories.common.ViewBuilder
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.add
import tornadofx.addClass

@ViewBuilder
fun EventTarget.emptyToolInvitation(
    orientation: Orientation = Orientation.VERTICAL,
    op: Parent.() -> Unit = {}
): Parent {
    val parent = when (orientation) {
        Orientation.VERTICAL -> VBox()
        Orientation.HORIZONTAL -> HBox()
    }
    parent.addClass(LayoutStyles.emptyToolInvitation)
    add(parent)
    parent.op()
    return parent
}

@ViewBuilder
fun EventTarget.emptyPrimaryToolInvitation(
    orientation: Orientation = Orientation.VERTICAL,
    op: Parent.() -> Unit = {}
): Parent = emptyToolInvitation(orientation) {
    addClass(LayoutStyles.primary)
    op()
}