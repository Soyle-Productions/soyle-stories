package com.soyle.stories.desktop.adapter.tools.scene

sealed class SceneListTool {

    object Loading : SceneListTool() {

        init {

        }

    }
    class Failed(val failure: Throwable) : SceneListTool()
    object Loaded : SceneListTool()

}