package com.soyle.stories.common.components

import com.soyle.stories.common.components.ChipStyles.Companion.chipDeleteIcon
import com.soyle.stories.soylestories.Styles
import com.soyle.stories.swing.soylestories.SoyleStories
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import tornadofx.*

class Chip(val node: Node) {

    val textProperty = SimpleStringProperty("")
    var text by textProperty

    val graphicProperty = SimpleObjectProperty<Node>(null)
    var graphic by graphicProperty

    val onDeleteProperty = SimpleObjectProperty<() -> Unit>(null)
    var onDelete by onDeleteProperty
}

fun EventTarget.chip(
    textProperty: ObservableValue<String>? = null,
    graphicProperty: ObservableValue<Node>? = null,
    onDelete: (() -> Unit)? = null,
    op: Chip.() -> Unit = {}
): Chip {
    val hbox = hbox {  }
    val chip = Chip(hbox)
    textProperty?.let { chip.textProperty.bind(it) }
    graphicProperty?.let { chip.graphicProperty.bind(it) }
    chip.onDelete = onDelete
    with(hbox) {
        addClass(ChipStyles.chip)
        alignment = Pos.CENTER_LEFT
        usePrefWidth = true
        label(chip.textProperty) {
            graphicProperty().bind(chip.graphicProperty)
        }
        button {
            graphic = MaterialIconView(MaterialIcon.DELETE_FOREVER, "1em").apply {
                setStyleClass(chipDeleteIcon.name)
            }
            minHeight = 0.0
            minWidthProperty().bind(heightProperty())
            alignment = Pos.CENTER
            visibleWhen(chip.onDeleteProperty.isNotNull)
            managedProperty().bind(visibleProperty())
            isPickOnBounds = true
        }
    }
    chip.op()
    return chip
}

class ChipStyles : Stylesheet() {

    companion object {
        val chip by cssclass()
        val chipDeleteIcon by cssclass()
        init {
            importStylesheet<ChipStyles>()
        }
    }

    init {
        chip {
            padding = box(0.px, 4.px, 0.px, 0.px)
            borderColor += box(Styles.Purple)
            borderWidth += box(1.px)
            prefHeight = 24.px
            borderRadius += box(16.px)
            label {
                padding = box(0.px, 8.px)
            }
            button {
                backgroundColor += Styles.Purple
                backgroundRadius += box(50.percent)
                maxHeight = 16.px
                maxWidth = 16.px
                accentColor = Color.TRANSPARENT
                focusColor = Color.TRANSPARENT
                hover {
                    backgroundColor = multi(Styles.Purple.darker())
                }
            }
        }
        chipDeleteIcon {
            textFill = Color.WHITESMOKE
            fill = Color.WHITESMOKE
        }
    }

}