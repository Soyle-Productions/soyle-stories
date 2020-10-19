package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.common.editingCell
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.themeList.ThemeList
import com.soyle.stories.theme.themeList.ThemeListItemViewModel
import com.soyle.stories.theme.themeList.ThemeListModel
import com.sun.javafx.tk.Toolkit
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.control.skin.TreeViewSkin
import javafx.scene.control.skin.VirtualFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.*
import tornadofx.FX
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.NoSuchElementException

fun WorkBench.givenThemeListToolHasBeenOpened(): ThemeList =
    getThemeListTool() ?: openThemeListTool()

fun WorkBench.getThemeListToolOrError(): ThemeList =
    getThemeListTool() ?: throw NoSuchElementException("Theme List has not been opened")

fun WorkBench.getThemeListTool(): ThemeList?
{
    return (FX.getComponents(scope)[ThemeList::class] as? ThemeList)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openThemeListTool(): ThemeList
{
    findMenuItemById("tools_themelist")!!
        .apply { robot.interact { fire() } }
    return getThemeListToolOrError()
}

fun ThemeList.renameThemeTo(originalName: String, newName: String)
{
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val treeItem = driver.getThemeItemOrError(originalName)
    val renameOptionItem = themeItemContextMenu.items.find { it.text == "Rename" }!!
    driver.interact {
        tree.selectionModel.select(treeItem)
        renameOptionItem.fire()
        (tree.editingCell!!.graphic as TextField).run {
            text = newName
            fireEvent(ActionEvent())
        }
    }
}

fun ThemeList.renameSymbolInThemeTo(originalSymbolName: String, themeName: String, newName: String)
{
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val themeItem = driver.getThemeItemOrError(themeName)
    val symbolItem = driver.getSymbolItemOrError(themeItem, originalSymbolName)
    val renameOptionItem = symbolItemContextMenu.items.find { it.text == "Rename" }!!
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
        renameOptionItem.fire()
        (tree.editingCell!!.graphic as TextField).run {
            text = newName
            fireEvent(ActionEvent())
        }
    }
}