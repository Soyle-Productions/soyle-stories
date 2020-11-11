package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.theme.themeList.ThemeListDriver
import com.soyle.stories.theme.deleteThemeDialog.DeleteThemeDialog
import com.soyle.stories.theme.themeList.ThemeList
import javafx.scene.control.Button
import tornadofx.uiComponent

/**
 * Can return null because the option to immediately delete the theme may have been checked
 */
fun ThemeList.openDeleteThemeDialogForThemeNamed(themeName: String): DeleteThemeDialog? {
    val driver = ThemeListDriver(this)
    val tree = driver.getTree()
    val treeItem = driver.getThemeItemOrError(themeName)
    val deleteOptionItem = themeItemContextMenu.items.find { it.text == "Delete" }!!
    robot.interact {
        tree.selectionModel.select(treeItem)
        deleteOptionItem.fire()
    }
    return getDeleteThemeDialog()
}

fun getDeleteThemeDialog(): DeleteThemeDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<DeleteThemeDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun DeleteThemeDialog.confirmDeleteTheme() {
    val confirmBtn = robot.from(this.root).lookup(".button").queryAll<Button>().find {
        it.isDefaultButton
    }!!
    robot.interact { confirmBtn.fire() }
}