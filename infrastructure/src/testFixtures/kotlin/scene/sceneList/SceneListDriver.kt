package com.soyle.stories.desktop.view.scene.sceneList

import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneList
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.testfx.api.FxRobot

class SceneListDriver(private val sceneList: SceneList) : FxRobot() {

    fun getTree(): TreeView<SceneItemViewModel?> = from(sceneList.root).lookup(".tree-view").query<TreeView<SceneItemViewModel?>>()

    fun getSceneItem(sceneName: String): TreeItem<SceneItemViewModel?>?
    {
        return getTree().root.children.asSequence().mapNotNull {
            val value = it.value as? SceneItemViewModel
            if (value != null && value.name == sceneName) it as TreeItem<SceneItemViewModel?>
            else null
        }.firstOrNull()
    }

}