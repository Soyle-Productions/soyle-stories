package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.common.editingCell
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.scene.sceneList.SceneListDriver
import com.soyle.stories.desktop.view.scene.sceneList.driver
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneList.SceneList
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import tornadofx.FX

fun WorkBench.givenSceneListToolHasBeenOpened(): SceneList =
    getSceneListTool() ?: openSceneListTool()

fun WorkBench.getSceneListToolOrError(): SceneList =
    getSceneListTool() ?: throw NoSuchElementException("Scene List has not been opened")

fun WorkBench.getSceneListTool(): SceneList?
{
    return (FX.getComponents(scope)[SceneList::class] as? SceneList)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openSceneListTool(): SceneList
{
    findMenuItemById("tools_scenelist")!!
        .apply { robot.interact { fire() } }
    return getSceneListToolOrError()
}

fun SceneList.selectScene(scene: Scene)
{
    with(driver()) {
        val sceneItem = getSceneItemOrError(scene.name.value)
        val tree = tree
        interact {
            tree.selectionModel.select(sceneItem)
        }
    }
}

fun SceneList.renameSceneTo(scene: Scene, newName: String)
{
    val driver = SceneListDriver(this)
    val sceneItem = driver.getSceneItemOrError(scene.name.value)
    val tree = driver.tree

    with(driver) {
        interact {
            tree.selectionModel.select(sceneItem)
            sceneItem.getRenameItem().fire()
            (tree.editingCell!!.graphic as TextField).run {
                text = newName
                fireEvent(ActionEvent())
            }
        }
    }
}

fun SceneList.deleteScene(scene: Scene)
{
    val driver = SceneListDriver(this)
    val sceneItem = driver.getSceneItemOrError(scene.name.value)
    val tree = driver.tree

    with(driver) {
        interact {
            tree.selectionModel.select(sceneItem)
            sceneItem.getDeleteItem().fire()
        }
    }
}