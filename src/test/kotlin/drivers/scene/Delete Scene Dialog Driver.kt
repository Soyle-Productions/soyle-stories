package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.scene.createSceneDialog.CreateSceneDialogDriver
import com.soyle.stories.desktop.view.scene.deleteSceneDialog.DeleteSceneDialogDriver
import com.soyle.stories.desktop.view.scene.sceneList.SceneListDriver
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialog
import com.soyle.stories.scene.sceneList.SceneList
import javafx.event.ActionEvent
import tornadofx.uiComponent

fun SceneList.givenDeleteSceneDialogHasBeenOpened(scene: Scene): DeleteSceneDialog =
    getDeleteSceneDialog() ?: openDeleteSceneDialog(scene)

fun SceneList.openDeleteSceneDialog(scene: Scene): DeleteSceneDialog {
    val driver = SceneListDriver(this)
    val tree = driver.getTree()
    val item = driver.getSceneItemOrError(scene.name.value)
    driver.interact {
        with (driver) {
            tree.selectionModel.select(item)
            item.getDeleteItem().fire()
        }
    }
    return getDeleteSceneDialogOrError()
}

fun getDeleteSceneDialogOrError(): DeleteSceneDialog =
    getDeleteSceneDialog() ?: throw NoSuchElementException("Delete Scene Dialog is not open in project")

fun getDeleteSceneDialog(): DeleteSceneDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<DeleteSceneDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun DeleteSceneDialog.confirmDelete()
{
    val driver = DeleteSceneDialogDriver(this)
    driver.interact {
        driver.confirmButton.fire()
    }
}