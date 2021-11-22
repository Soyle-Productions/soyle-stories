package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.desktop.view.scene.deleteSceneDialog.DeleteSceneDialogDriver
import com.soyle.stories.desktop.view.scene.sceneList.drive
import com.soyle.stories.desktop.view.scene.sceneList.driver
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.delete.DeleteScenePromptView
import com.soyle.stories.scene.sceneList.SceneListView

fun WorkBench.givenDeleteSceneDialogHasBeenOpened(scene: Scene): DeleteScenePromptView =
    getOpenDeleteSceneDialog(scene) ?: givenSceneListToolHasBeenOpened().openDeleteSceneDialog(scene)
        .run { getOpenDeleteSceneDialogOrError(scene) }

fun WorkBench.getOpenDeleteSceneDialogOrError(scene: Scene): DeleteScenePromptView =
    getOpenDeleteSceneDialog(scene) ?: error("Delete scene dialog for ${scene.name} is not open")

fun WorkBench.getOpenDeleteSceneDialog(scene: Scene): DeleteScenePromptView? =
    robot.getOpenDialog<DeleteScenePromptView>()


fun SceneListView.openDeleteSceneDialog(scene: Scene) {
    val sceneItem = driver().getSceneItemOrError(scene.name.value)
    drive {
        tree.selectionModel.select(sceneItem)
        sceneItem.getDeleteItem()
            .fire()
    }
}

fun DeleteScenePromptView.confirmDelete() {
    val driver = DeleteSceneDialogDriver(this)
    driver.interact {
        driver.confirmButton.fire()
    }
}