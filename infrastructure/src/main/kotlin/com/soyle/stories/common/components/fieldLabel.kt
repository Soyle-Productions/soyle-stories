package com.soyle.stories.common.components

import javafx.beans.property.ReadOnlyProperty
import javafx.event.EventTarget
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import tornadofx.*

fun EventTarget.fieldLabel(text: String = "") = hbox {
    hgrow = Priority.ALWAYS
    label(text) {
        addClass(Styles.fieldLabel)
    }
}
fun EventTarget.fieldLabel(textProperty: ReadOnlyProperty<String>) = hbox {
    hgrow = Priority.ALWAYS
    label(textProperty) {
        addClass(Styles.fieldLabel)
    }
}

class Styles : Stylesheet() {

    companion object {
        val fieldLabel by cssclass()
        init {
            importStylesheet<Styles>()
        }
    }

    init {
        fieldLabel {
            fontWeight = FontWeight.BOLD
            fontSize = 1.2.em
            padding = box(0.px, 0.px, 5.px, 0.px)
        }
    }

}