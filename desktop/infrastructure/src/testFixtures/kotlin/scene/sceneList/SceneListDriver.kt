package com.soyle.stories.desktop.view.scene.sceneList

import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneListView
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.testfx.api.FxRobot

class SceneListDriver(private val sceneList: SceneListView) : FxRobot() {

    val tree: TreeView<SceneItemViewModel?>
        get() = from(sceneList.root).lookup(".tree-view").query<TreeView<SceneItemViewModel?>>()

    fun getSceneItemOrError(sceneName: String): TreeItem<SceneItemViewModel?> =
        getSceneItem(sceneName) ?: error("Scene List does not contain scene named $sceneName")

    fun getSceneItem(sceneName: String): TreeItem<SceneItemViewModel?>?
    {
        return tree.root.children.asSequence().mapNotNull {
            val value = it.value as? SceneItemViewModel
            if (value != null && value.name == sceneName) it as TreeItem<SceneItemViewModel?>
            else null
        }.firstOrNull()
    }

    fun TreeItem<SceneItemViewModel?>.getSceneEditorItem(): MenuItem = sceneList.sceneContextMenu.items.find { it.id == "edit" }!!
    fun TreeItem<SceneItemViewModel?>.getSceneDetailsItem(): MenuItem = sceneList.sceneContextMenu.items.find { it.id == "open_details" }!!
    fun TreeItem<SceneItemViewModel?>.getRenameItem(): MenuItem = sceneList.sceneContextMenu.items.find { it.id == "rename" }!!
    fun TreeItem<SceneItemViewModel?>.getDeleteItem(): MenuItem = sceneList.sceneContextMenu.items.find { it.id == "delete" }!!

}

fun SceneListView.driver() = SceneListDriver(this)
inline fun SceneListView.drive(crossinline road: SceneListDriver.() -> Unit) {
    val driver = SceneListDriver(this)
    driver.interact { driver.road() }
}