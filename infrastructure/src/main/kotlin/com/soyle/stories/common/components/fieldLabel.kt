package com.soyle.stories.common.components

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.FontWeight
import tornadofx.*


fun EventTarget.fieldLabel(text: String = "") = hbox {
    addClass(Styles.fieldLabelContainer)
    hgrow = Priority.ALWAYS
    label(text) {
        addClass(Styles.fieldLabel)
    }
}
fun EventTarget.fieldLabel(textProperty: ObservableValue<String>) = hbox {
    addClass(Styles.fieldLabelContainer)
    hgrow = Priority.ALWAYS
    label(textProperty) {
        addClass(Styles.fieldLabel)
    }
}

fun EventTarget.labeledSection(text: String = "", op: VBox.() -> Unit = {}) = vbox {
    addClass(Styles.labeledSection)
    fieldLabel(text)
    op()
}
fun EventTarget.labeledSection(textProperty: ObservableValue<String>, op: VBox.() -> Unit = {}) = vbox {
    addClass(Styles.labeledSection)
    fieldLabel(textProperty)
    op()
}

class Styles : Stylesheet() {

    companion object {
        val labeledSection by cssclass()
        val fieldLabelContainer by cssclass()
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