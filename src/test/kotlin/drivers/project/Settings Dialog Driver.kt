package com.soyle.stories.desktop.config.drivers.project

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.scene.getCreateSceneDialogOrError
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.project.settingsDialog.SettingsDialogDriver
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import com.soyle.stories.writer.DialogType
import com.soyle.stories.writer.settingsDialog.SettingsDialog
import javafx.scene.control.CheckBox
import tornadofx.uiComponent

fun WorkBench.givenSettingsDialogHasBeenOpened(): SettingsDialog =
    getOpenSettingsDialog() ?: openSettingsDialog()

fun WorkBench.getOpenSettingsDialogOrError(): SettingsDialog =
    getOpenSettingsDialog() ?: error("Settings Dialog is not open")

fun WorkBench.getOpenSettingsDialog(): SettingsDialog?
{
    return robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<SettingsDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }
}

fun WorkBench.openSettingsDialog(): SettingsDialog
{
    findMenuItemById("file_settings")!!
        .apply { robot.interact { fire() } }
    return getOpenSettingsDialogOrError()
}

fun SettingsDialog.markConfirmDeleteSceneDialogUnNecessary()
{
    val driver = SettingsDialogDriver(this)
    val checkBox = driver.getDialogCheckbox(DialogType.DeleteScene)
    if (checkBox.isSelected) {
        robot.interact {
            checkBox.isSelected = false
            driver.getSaveButton().fire()
        }
    }
}