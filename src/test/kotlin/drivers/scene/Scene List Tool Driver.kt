package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.config.drivers.theme.getThemeListToolOrError
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneList.SceneList
import com.soyle.stories.theme.themeList.ThemeList
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