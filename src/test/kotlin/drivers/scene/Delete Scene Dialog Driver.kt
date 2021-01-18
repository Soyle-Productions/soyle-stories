package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.scene.deleteSceneDialog.DeleteSceneDialogDriver
import com.soyle.stories.desktop.view.scene.sceneList.drive
import com.soyle.stories.desktop.view.scene.sceneList.driver
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialog
import com.soyle.stories.scene.sceneList.SceneList

fun WorkBench.givenDeleteSceneDialogHasBeenOpened(scene: Scene): DeleteSceneDialog =
    getOpenDeleteSceneDialog(scene) ?: givenSceneListToolHasBeenOpened().openDeleteSceneDialog(scene)
        .run { getOpenDeleteSceneDialogOrError(scene) }

fun WorkBench.getOpenDeleteSceneDialogOrError(scene: Scene): DeleteSceneDialog =
    getOpenDeleteSceneDialog(scene) ?: error("Delete scene dialog for ${scene.name} is not open")

fun WorkBench.getOpenDeleteSceneDialog(scene: Scene): DeleteSceneDialog? =
    robot.getOpenDialog<DeleteSceneDialog>()?.takeIf { it.sceneId == scene.id.uuid.toString() }


fun SceneList.openDeleteSceneDialog(scene: Scene) {
    val sceneItem = driver().getSceneItemOrError(scene.name.value)
    drive {
        tree.selectionModel.select(sceneItem)
        sceneItem.getDeleteItem()
            .fire()
    }
}

fun DeleteSceneDialog.confirmDelete() {
    val driver = DeleteSceneDialogDriver(this)
    driver.interact {
        driver.confirmButton.fire()
    }
}