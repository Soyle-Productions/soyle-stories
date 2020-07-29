package com.soyle.stories.common.components

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import tornadofx.*

fun EventTarget.responsiveBox(isSmall: ObservableValue<Boolean>, hSpacing: Double = 0.0, vSpacing: Double = 0.0, op: Pane.() -> Unit) {

    val stackpane = stackpane()
    val vBox = VBox(vSpacing)
    val hBox = HBox(hSpacing)

    stackpane.op()

    val children = stackpane.childrenUnmodifiable.toList()

    fun smallLayout() {
        vBox.children.clear()
        stackpane.children.clear()
        stackpane.children.add(vBox)
        vBox.children.addAll(children)
    }
    fun largeLayout() {
        hBox.children.clear()
        stackpane.children.clear()
        stackpane.children.add(hBox)
        hBox.children.addAll(children)
    }

    isSmall.onChange {
        if (it == true) smallLayout()
        else largeLayout()
    }

    largeLayout()

}
fun EventTarget.responsiveBox(threshold: Int = 600, hSpacing: Double = 0.0, vSpacing: Double = 0.0, op: Pane.() -> Unit) {

    val stackpane = stackpane()
    val vBox = VBox(vSpacing)
    val hBox = HBox(hSpacing)

    stackpane.op()

    val children = stackpane.childrenUnmodifiable.toList()

    fun smallLayout() {
        vBox.children.clear()
        stackpane.children.clear()
        stackpane.children.add(vBox)
        vBox.children.addAll(children)
    }
    fun largeLayout() {
        hBox.children.clear()
        hBox.children.addAll(children)
        stackpane.children.clear()
        stackpane.children.add(hBox)
    }

    stackpane.widthProperty().onChange {
        if (it < threshold) smallLayout()
        else largeLayout()
    }

    largeLayout()

}