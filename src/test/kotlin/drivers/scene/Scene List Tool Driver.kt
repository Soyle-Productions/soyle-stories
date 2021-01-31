package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.common.editingCell
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.scene.sceneList.SceneListDriver
import com.soyle.stories.desktop.view.scene.sceneList.driver
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneList.SceneListItem
import com.soyle.stories.scene.sceneList.SceneListView
import javafx.event.ActionEvent
import tornadofx.FX
import tornadofx.uiComponent

fun WorkBench.givenSceneListToolHasBeenOpened(): SceneListView =
    getSceneListTool() ?: openSceneListTool()

fun WorkBench.getSceneListToolOrError(): SceneListView =
    getSceneListTool() ?: throw NoSuchElementException("Scene List has not been opened")

fun WorkBench.getSceneListTool(): SceneListView?
{
    return (FX.getComponents(scope)[SceneListView::class] as? SceneListView)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openSceneListTool(): SceneListView
{
    findMenuItemById("tools_scenelist")!!
        .apply { robot.interact { fire() } }
    return getSceneListToolOrError()
}

fun SceneListView.selectScene(scene: Scene)
{
    with(driver()) {
        val sceneItem = getSceneItemOrError(scene.name.value)
        val tree = tree
        interact {
            tree.selectionModel.select(sceneItem)
        }
    }
}

fun SceneListView.renameSceneTo(scene: Scene, newName: String)
{
    val driver = SceneListDriver(this)
    val sceneItem = driver.getSceneItemOrError(scene.name.value)
    val tree = driver.tree

    with(driver) {
        interact {
            tree.selectionModel.select(sceneItem)
            sceneItem.getRenameItem().fire()
            val sceneUIItem = tree.editingCell!!.graphic!!.uiComponent<SceneListItem>()!!
            from(sceneUIItem.root).lookup(".text-field").queryTextInputControl().run {
                text = newName
                fireEvent(ActionEvent())
            }
        }
    }
}

fun SceneListView.deleteScene(scene: Scene)
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