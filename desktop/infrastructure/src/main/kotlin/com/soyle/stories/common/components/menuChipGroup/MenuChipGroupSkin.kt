package com.soyle.stories.common.components.menuChipGroup

import com.soyle.stories.common.components.Chip
import com.soyle.stories.common.components.ChipNode
import com.soyle.stories.common.components.chip
import com.soyle.stories.common.exists
import com.soyle.stories.common.onChangeUntil
import com.sun.javafx.scene.NodeHelper
import com.sun.javafx.scene.control.ContextMenuContent
import com.sun.javafx.scene.control.ControlAcceleratorSupport
import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.collections.SetChangeListener
import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.Mnemonic
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import tornadofx.*
import kotlin.math.max

class MenuChipGroupSkin(menuChipGroup: MenuChipGroup) : SkinBase<MenuChipGroup>(menuChipGroup) {

    private val arrowButton: StackPane
    private val chipContainer: FlowPane

    private val disposeListeners = mutableListOf<() -> Unit>()
    private fun onDispose(listener: () -> Unit) = disposeListeners.add(listener)

    init {
        children.clear()
        chipContainer = FlowPane().apply {
            vgap = 5.0
            hgap = 5.0
            label(menuChipGroup.noSelectionTextProperty) {
                disappearWhenThereAreChips()
            }
            createChipsWhenSelectionChanges()
        }
        arrowButton = StackPane().apply {
            addClass(MenuChipGroupStyles.arrowButton)
            stackpane {
                addClass(MenuChipGroupStyles.arrow)
                maxWidth = Region.USE_PREF_SIZE
                maxHeight = Region.USE_PREF_SIZE
            }
        }
        children.addAll(chipContainer, arrowButton)
        menuChipGroup.requestLayout()
    }

    private var popup: ContextMenu? = ContextMenu().apply {
        addAcceleratorsWhenSceneChanges()
        bindItemsToSkinnable()
    }

    private fun Label.disappearWhenThereAreChips() {
        exists = skinnable.chips.isEmpty()
        val chipListener = ListChangeListener<Chip> {
            exists = it.list.isEmpty()
        }
        onDispose { skinnable.chips.removeListener(chipListener) }
        skinnable.chips.addListener(chipListener)
    }

    private fun Pane.createChipsWhenSelectionChanges() {
        skinnable.chips.forEach { children.add(it.node) }
        val chipListener = ListChangeListener<Chip> { change ->
            while(change.next()) {
                if (change.wasAdded()) children.addAll(change.addedSubList.map { it.node })
                if (change.wasRemoved()) children.removeAll(change.removed.map { it.node })
            }
            requestLayout()
        }
        onDispose { skinnable.chips.removeListener(chipListener) }
        skinnable.chips.addListener(chipListener)
    }

    private fun ContextMenu.addAcceleratorsWhenSceneChanges() {
        val sceneChangeListener = ChangeListener<Scene> { observable, oldValue, newValue ->
            val control = skinnable ?: return@ChangeListener
            control.scene ?: return@ChangeListener
            ControlAcceleratorSupport.addAcceleratorsIntoScene(items, control)
        }
        onDispose { skinnable.sceneProperty().removeListener(sceneChangeListener) }
        skinnable.sceneProperty().addListener(sceneChangeListener)
        ControlAcceleratorSupport.addAcceleratorsIntoScene(items, skinnable)
    }

    private fun ContextMenu.bindItemsToSkinnable() {
        val itemBinding = items.bind(skinnable.items) { it }
        onDispose { skinnable.items.removeListener(itemBinding) }
    }

    private var behavior: MenuChipGroupBehavior? = MenuChipGroupBehavior(menuChipGroup)

    init {

        if (menuChipGroup.onMousePressed == null) {
            menuChipGroup.addEventHandler(MouseEvent.MOUSE_PRESSED) {
                behavior?.onMousePressed(it)
            }
        }

        if (menuChipGroup.onMouseReleased == null) {
            menuChipGroup.addEventHandler(MouseEvent.MOUSE_RELEASED) {
                behavior?.onMouseReleased(it)
            }
        }

        registerChangeListener(menuChipGroup.showingProperty()) {
            skinnable?.let {
                if (it.isShowing) show()
                else hide()
            }
        }
        registerChangeListener(menuChipGroup.focusedProperty()) {
            skinnable?.let {
                if (! it.isFocused && it.isShowing) hide()
                if (! it.isFocused && popup?.isShowing == true) hide()
            }
        }
        val mnemonics: MutableList<Mnemonic> = mutableListOf()
        // if [popup] is null at this point, something very weird went wrong (probably another thread updated it somehow
        // or someone modified it so that [popup] isn't created with the Skin.
        registerChangeListener(popup!!.showingProperty()) {
            // popup could be null here, though.
            popup?.let {
                if (! it.isShowing && skinnable?.isShowing == true) {
                    skinnable?.hide()
                }
                if (it.isShowing) {
                    val showMnemonics = NodeHelper.isShowMnemonics(skinnable)
                    com.sun.javafx.scene.control.skin.Utils.addMnemonics(
                        popup,
                        skinnable.scene,
                        showMnemonics,
                        mnemonics
                    )
                } else {
                    // we wrap this in a runLater so that mnemonics are not removed
                    // before all key events are fired (because KEY_PRESSED might have
                    // been used to hide the menu, but KEY_TYPED and KEY_RELEASED
                    // events are still to be fired, and shouldn't miss out on going
                    // through the mnemonics code (especially in case they should be
                    // consumed to prevent them being used elsewhere).
                    // See JBS-8090026 for more detail.
                    val scene = skinnable.scene
                    val mnemonicsToRemove: List<Mnemonic> = mnemonics.toList()
                    mnemonics.clear()
                    Platform.runLater { mnemonicsToRemove.forEach(scene::removeMnemonic) }
                }
            }
        }

        popup!!.setOnAutoHide {
            skinnable?.let {
                if (! it.properties.containsKey(AUTOHIDE)) {
                    it.properties[AUTOHIDE] = true
                }
            }
        }

        popup!!.setOnShown {
            (popup?.skin?.node as? ContextMenuContent)?.requestFocus()
        }

        if (menuChipGroup.onAction == null) menuChipGroup.action { skinnable.show() }

    }

    override fun dispose() {
        disposeListeners.forEach { it.invoke() }
        disposeListeners.clear()
        super.dispose()
        popup?.let {
            if (it.skin != null && it.skin.node != null) {
                val cmContent = it.skin.node as ContextMenuContent
                cmContent.dispose()
            }
            it.skin = null
            it.items.clear()
            popup = null
        }
        behavior?.dispose()
    }

    /** {@inheritDoc}  */
    override fun computeMinWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (leftInset
                + chipContainer.minWidth(height)
                + snapSizeX(arrowButton.minWidth(height))
                + rightInset)
    }

    /** {@inheritDoc}  */
    override fun computeMinHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (topInset
                + max(chipContainer.minHeight(width), snapSizeY(arrowButton.minHeight(-1.0)))
                + bottomInset)
    }

    /** {@inheritDoc}  */
    override fun computePrefWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (leftInset
                + chipContainer.prefWidth(height)
                + snapSizeX(arrowButton.prefWidth(height))
                + rightInset)
    }

    /** {@inheritDoc}  */
    override fun computePrefHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return (topInset
                + max(chipContainer.prefHeight(width), snapSizeY(arrowButton.prefHeight(-1.0)))
                + bottomInset)
    }

    /** {@inheritDoc}  */
    override fun computeMaxWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return skinnable.prefWidth(height)
    }

    /** {@inheritDoc}  */
    override fun computeMaxHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return skinnable.prefHeight(width)
    }

    /** {@inheritDoc}  */
    override fun layoutChildren(
        x: Double, y: Double,
        w: Double, h: Double
    ) {
        val arrowButtonWidth = snapSizeX(arrowButton.prefWidth(-1.0))
        chipContainer.resizeRelocate(x, y, w - arrowButtonWidth, h)
        arrowButton.resizeRelocate(x + (w - arrowButtonWidth), y, arrowButtonWidth, h)
    }

    private fun show() {
        popup?.let {
            if (! it.isShowing) {
                it.show(skinnable, Side.BOTTOM, 0.0, 0.0)
            }
        }
    }

    private fun hide() {
        popup?.let {
            if (it.isShowing) {
                it.hide()
            }
        }
    }

    companion object {
        private const val AUTOHIDE = "autoHide"
    }

}