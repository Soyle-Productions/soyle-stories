package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.scene.createSceneDialog.CreateSceneDialogDriver
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.create.CreateScenePromptView
import javafx.event.ActionEvent
import tornadofx.uiComponent

fun WorkBench.openCreateSceneDialog(): CreateScenePromptView {
    findMenuItemById("file_new_scene")!!
        .apply { robot.interact { fire() } }
    return getCreateSceneDialogOrError()
}

fun getCreateSceneDialogOrError(): CreateScenePromptView =
    getCreateSceneDialog() ?: throw NoSuchElementException("Create Scene Dialog is not open in project")

fun getCreateSceneDialog(): CreateScenePromptView? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<CreateScenePromptView>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun CreateScenePromptView.createSceneWithName(sceneName: String)
{
    val driver = CreateSceneDialogDriver(this)
    val nameInput = driver.getNameInput()
    driver.interact {
        nameInput.text = sceneName
        nameInput.fireEvent(ActionEvent())
    }
}