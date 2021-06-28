package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.theme.deleteSymbolDialog.DeleteSymbolDialog
import com.soyle.stories.theme.themeList.ThemeList
import com.sun.javafx.tk.Toolkit
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import tornadofx.uiComponent

/**
 * Can return null because the option to immediately delete the theme may have been checked
 */
fun ThemeList.openDeleteSymbolDialogForSymbolInThemeNamed(themeName: String, symbolName: String): DeleteSymbolDialog? {
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val themeItem = driver.getThemeItemOrError(themeName)
    val symbolItem = driver.getSymbolItemOrError(themeItem, symbolName)
    val deleteOptionItem = symbolItemContextMenu.items.find { it.text == "Delete" }!!
    driver.interact {
        if (!themeItem.isExpanded) {
            tree.requestFocus()
            themeItem.isExpanded = true
        }
        // Unsure of cause, but the VirtualFlow within the treeview has not yet updated the cells at this point.  By
        // dragging the SplitPane a bit, it somehow forced a redraw and made the cell render correctly.  So, we find the
        // first SplitPane parent and request a layout from it and that seems to make the cells get rendered properly.
        // Note: TreeView.refresh() had no affect.
        var node = root
        while (node !is SplitPane) {
            node = node.parent ?: break
        }
        node.requestLayout()
        Toolkit.getToolkit().firePulse()
    }
    driver.interact {
        tree.selectionModel.select(symbolItem)
        deleteOptionItem.fire()
    }
    return getDeleteSymbolDialog()
}

fun ThemeList.openDeleteSymbolDialogForSymbolInTheme(theme: Theme, symbolName: String): DeleteSymbolDialog? =
    openDeleteSymbolDialogForSymbolInThemeNamed(theme.name, symbolName)

fun getDeleteSymbolDialog(): DeleteSymbolDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<DeleteSymbolDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }


fun DeleteSymbolDialog.confirmDeleteSymbol() {
    val confirmBtn = robot.from(this.root).lookup(".button").queryAll<Button>().find {
        it.isDefaultButton
    }!!
    robot.interact { confirmBtn.fire() }
}