package com.soyle.stories.common.components.menuChipGroup

import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.KeyCombination
import tornadofx.action
import tornadofx.addChildIfPossible
import tornadofx.menu
import tornadofx.plusAssign

fun EventTarget.menuchipgroup(op: MenuChipGroup.() -> Unit) = MenuChipGroup()
    .apply(op)
    .also { addChildIfPossible(it) }

//MenuChipGroup extensions
fun MenuChipGroup.menu(name: String? = null, op: Menu.() -> Unit = {}) = Menu(name).also {
    op(it)
    items += it
}

fun MenuChipGroup.checkmenuitem(
    name: String, keyCombination: KeyCombination? = null, graphic: Node? = null, op: CheckMenuItem.() -> Unit = {}
) = CheckMenuItem(name, graphic).also {
    keyCombination?.apply { it.accelerator = this }
    graphic?.apply { it.graphic = graphic }
    op(it)
    items += it
}

/**
 * Create a MenuItem. The op block operates on the MenuItem where you can call `setOnAction` to provide the menu item action. Notice that this differs
 * from the deprecated `menuitem` builder where the op is configured as the `setOnAction` directly.
 */
fun MenuChipGroup.item(
    name: String, keyCombination: KeyCombination? = null, graphic: Node? = null, op: MenuItem.() -> Unit = {}
) = MenuItem(name, graphic).also {
    keyCombination?.apply { it.accelerator = this }
    graphic?.apply { it.graphic = this }
    op(it)
    items += it
}

/**
 * Create a MenuItem with the name property bound to the given observable string. The op block operates on the MenuItem where you can
 * call `setOnAction` to provide the menu item action. Notice that this differs from the deprecated `menuitem` builder where the op
 * is configured as the `setOnAction` directly.
 */
fun MenuChipGroup.item(
    name: ObservableValue<String>, keyCombination: KeyCombination? = null, graphic: Node? = null, op: MenuItem.() -> Unit = {}
) = MenuItem(null, graphic).also {
    it.textProperty().bind(name)
    keyCombination?.apply { it.accelerator = this }
    graphic?.apply { it.graphic = this }
    op(it)
    items += it
}

/**
 * Add a separator to the menuchipgroup
 */
fun MenuChipGroup.separator(op: SeparatorMenuItem.() -> Unit = {}) {
    items += SeparatorMenuItem().also(op)
}