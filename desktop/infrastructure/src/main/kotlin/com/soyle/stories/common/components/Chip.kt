package com.soyle.stories.common.components

import com.soyle.stories.common.components.ChipStyles.Companion.chipDeleteIcon
import com.soyle.stories.common.exists
import com.soyle.stories.common.existsWhen
import com.soyle.stories.soylestories.Styles
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.Skin
import javafx.scene.control.skin.LabeledSkinBase
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.math.max

class ChipNode : Labeled() {

    init {
        addClass(ChipStyles.chip)
    }

    val onActionProperty = SimpleObjectProperty<EventHandler<ActionEvent>>(null)
    var onAction: EventHandler<ActionEvent>? by onActionProperty
    fun action(handler: EventHandler<ActionEvent>) { onAction = handler }

    override fun createDefaultSkin(): Skin<*> = ChipSkin(this)

    override fun getUserAgentStylesheet(): String = ChipStyles().externalForm

}

class ChipSkin(chip: ChipNode) : LabeledSkinBase<ChipNode>(chip)
{
    private val label = Label().apply {
        registerChangeListener(skinnable.graphicProperty()) {
            graphic = skinnable?.graphic
        }
        registerChangeListener(skinnable.textProperty()) {
            text = skinnable?.text
        }
    }
    private val deleteButton = Button().apply {
        registerChangeListener(skinnable.onActionProperty) {
            onAction = skinnable?.onAction
            exists = skinnable?.onAction != null
        }

        graphic = MaterialIconView(MaterialIcon.DELETE_FOREVER, "1em").apply {
            setStyleClass(chipDeleteIcon.name)
        }
    }

    init {
        children.clear()
        children.addAll(label, deleteButton)
        skinnable.requestLayout()
    }

    override fun computeMaxHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return skinnable.prefHeight(width)
    }

    override fun computeMaxWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return skinnable.prefWidth(height)
    }

    override fun computeMinHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (topInset
                + max(
            label.minHeight(width), if (deleteButton.managedProperty().get()) snapSizeY(
                deleteButton.minHeight(
                    -1.0
                )
            ) else 0.0
        )
                + bottomInset)
    }

    override fun computeMinWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (leftInset
                + label.minWidth(height)
                + (if (deleteButton.managedProperty().get()) snapSizeX(deleteButton.minWidth(height)) else 0.0)
                + rightInset)
    }

    override fun computePrefHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (topInset
                + max(label.prefHeight(width), if (deleteButton.managedProperty().get()) snapSizeY(deleteButton.prefHeight(-1.0)) else 0.0)
                + bottomInset)
    }

    override fun computePrefWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (leftInset
                + label.prefWidth(height)
                + (if (deleteButton.managedProperty().get()) snapSizeX(deleteButton.prefWidth(height)) else 0.0)
                + rightInset)
    }

    override fun layoutChildren(x: Double, y: Double, w: Double, h: Double) {
        val arrowButtonWidth = if (deleteButton.managedProperty().get()) snapSizeX(deleteButton.prefWidth(-1.0)) else 0.0
        label.resizeRelocate(x, y, w - arrowButtonWidth, h)
        deleteButton.resizeRelocate(x + (w - arrowButtonWidth), y, arrowButtonWidth, h)
    }
}

class Chip(val node: Node) {

    val textProperty = SimpleStringProperty("")
    var text by textProperty

    val graphicProperty = SimpleObjectProperty<Node>(null)
    var graphic by graphicProperty

    val onDeleteProperty = SimpleObjectProperty<(Event) -> Unit>(null)
    var onDelete by onDeleteProperty
    fun onDelete(op: (Event) -> Unit) {
        onDelete = op
    }
}

fun EventTarget.chip(
    textProperty: ObservableValue<String>? = null,
    graphicProperty: ObservableValue<Node>? = null,
    onDelete: ((Event) -> Unit)? = null,
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
            setOnAction {
                chip.onDelete?.invoke(it)
            }
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