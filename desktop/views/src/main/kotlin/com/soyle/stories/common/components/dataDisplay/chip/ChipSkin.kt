package com.soyle.stories.common.components.dataDisplay.chip

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.surfaces.*
import com.soyle.stories.common.existsWhen
import javafx.event.ActionEvent
import javafx.scene.control.Label
import javafx.scene.control.SkinBase
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.Region.USE_PREF_SIZE
import javafx.scene.layout.StackPane
import tornadofx.addClass
import tornadofx.toggleClass

class ChipSkin(chip: Chip) : SkinBase<Chip>(chip) {

    private val label = Label().apply {
        minWidth = USE_PREF_SIZE
        textProperty().bind(skinnable.textProperty())
        graphicProperty().bind(skinnable.graphicProperty())
    }

    private val deleteButton = StackPane().apply {
        existsWhen(skinnable.onDeleteProperty().isNotNull)
        addClass(Chip.Styles.chipDeleteIcon)
        setOnMouseClicked {
            it.consume()
            skinnable?.onDelete?.handle(ActionEvent())
        }
        setOnKeyPressed { if (it.code == KeyCode.ENTER) skinnable?.onDelete?.handle(ActionEvent()) }
        focusTraversableProperty().bind(skinnable.onDeleteProperty().isNotNull)
        children.setAll(skinnable.deleteGraphic)
        registerChangeListener(skinnable.deleteGraphicProperty()) {
            children.setAll(skinnable?.deleteGraphic)
        }
    }

    private val container = HBox(label, deleteButton).apply {
        addClass(Chip.Styles.chipRoot)
        setOnMouseClicked { skinnable?.onAction?.handle(ActionEvent()) }
        setOnKeyPressed { if (it.code == KeyCode.ENTER) skinnable?.onAction?.handle(ActionEvent()) }
        focusTraversableProperty().bind(skinnable.onActionProperty().isNotNull)
    }

    private fun toggleColorClasses() {
        val chip = skinnable ?: return
        chip.toggleClass(ComponentsStyles.primary, chip.color == Chip.Color.primary)
        chip.toggleClass(ComponentsStyles.secondary, chip.color == Chip.Color.secondary)
    }

    private fun toggleVariantClasses() {
        val chip = skinnable ?: return
        chip.toggleClass(ComponentsStyles.filled, chip.variant == Chip.Variant.default)
        chip.toggleClass(ComponentsStyles.outlined, chip.variant == Chip.Variant.outlined)
    }

    init {
        children.clear()
        children.add(container)
        if (! skinnable.elevationProperty().isBound) skinnable.elevation = Elevation.getValue(4)
        if (! skinnable.elevationVariantProperty().isBound) skinnable.elevationVariant = outlined
        registerChangeListener(chip.colorProperty()) {
            toggleColorClasses()
        }
        registerChangeListener(chip.variantProperty()) {
            toggleVariantClasses()
        }
        toggleColorClasses()
        toggleVariantClasses()
    }
}