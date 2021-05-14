package com.soyle.stories.common.components.text

import com.soyle.stories.common.ViewBuilder
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.Label
import tornadofx.attachTo
import tornadofx.label

@ViewBuilder
fun EventTarget.resizableLabel(text: String = "", graphic: Node? = null, op: Label.() -> Unit = {}) =
    ResizableLabel(text).attachTo(this, op) {
        if (graphic != null) it.graphic = graphic
    }

fun ResizableLabel(text: String = "") = Label(text).resizable()

fun Label.resizable() = apply { maxWidth = Double.MAX_VALUE }