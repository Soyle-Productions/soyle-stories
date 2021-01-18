package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.config.drivers.theme.getCreateThemeDialogOrError
import com.soyle.stories.desktop.view.scene.createSceneDialog.CreateSceneDialogDriver
import com.soyle.stories.desktop.view.theme.createThemeDialog.CreateThemeDialogDriver
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import javafx.event.ActionEvent
import tornadofx.uiComponent

fun WorkBench.openCreateSceneDialog(): CreateSceneDialog {
    findMenuItemById("file_new_scene")!!
        .apply { robot.interact { fire() } }
    return getCreateSceneDialogOrError()
}

fun getCreateSceneDialogOrError(): CreateSceneDialog =
    getCreateSceneDialog() ?: throw NoSuchElementException("Create Scene Dialog is not open in project")

fun getCreateSceneDialog(): CreateSceneDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<CreateSceneDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun CreateSceneDialog.createSceneWithName(sceneName: String)
{
    val driver = CreateSceneDialogDriver(this)
    val nameInput = driver.getNameInput()
    driver.interact {
        nameInput.text = sceneName
        nameInput.fireEvent(ActionEvent())
    }
}