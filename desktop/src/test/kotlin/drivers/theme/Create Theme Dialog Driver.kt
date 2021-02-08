package com.soyle.stories.desktop.config.drivers.theme

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.theme.createThemeDialog.CreateThemeDialogDriver
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.project.WorkBench
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import tornadofx.uiComponent

fun WorkBench.givenCreateThemeDialogHasBeenOpened(): CreateThemeDialog =
    getCreateThemeDialog() ?: findMenuItemById("file_new_theme")!!
        .apply { robot.interact { fire() } }
        .let { getCreateThemeDialogOrError() }

fun WorkBench.openCreateThemeDialog(): CreateThemeDialog {
    findMenuItemById("file_new_theme")!!
        .apply { robot.interact { fire() } }
    return getCreateThemeDialogOrError()
}

fun getCreateThemeDialogOrError(): CreateThemeDialog =
    getCreateThemeDialog() ?: throw NoSuchElementException("Create Theme Dialog is not open in project")

fun getCreateThemeDialog(): CreateThemeDialog? =
    robot.listWindows().asSequence()
    .mapNotNull { it.scene.root.uiComponent<CreateThemeDialog>() }
    .firstOrNull { it.currentStage?.isShowing == true }

fun CreateThemeDialog.createThemeWithName(themeName: String)
{
    val driver = CreateThemeDialogDriver(this)
    val nameInput = driver.getNameInput()
    driver.interact {
        nameInput.text = themeName
        nameInput.fireEvent(ActionEvent())
    }
}
