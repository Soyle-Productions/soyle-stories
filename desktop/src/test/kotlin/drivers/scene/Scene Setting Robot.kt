package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.scene.sceneSetting.drive
import com.soyle.stories.desktop.view.scene.sceneSetting.driver
import com.soyle.stories.di.get
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.sceneSetting.SceneSettingView
import tornadofx.FX

fun WorkBench.givenSceneSettingToolHasBeenOpened(): SceneSettingView =
    getOpenSceneSettingsTool() ?: openSceneSettingsTool().run { getOpenSceneSettingsToolOrError() }

fun WorkBench.getOpenSceneSettingsToolOrError(): SceneSettingView =
    getOpenSceneSettingsTool() ?: error("No Scene Setting tool is open in the project")

fun WorkBench.getOpenSceneSettingsTool(): SceneSettingView?
{
    return (FX.getComponents(scope)[SceneSettingView::class] as? SceneSettingView)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openSceneSettingsTool()
{
    findMenuItemById("tools_scene setting")!!
        .apply { robot.interact { fire() } }
}

fun SceneSettingView.givenFocusedOn(scene: Scene): SceneSettingView
{
    if (! driver().isFocusedOn(scene)) focusOn(scene)
    return this
}

fun SceneSettingView.focusOn(scene: Scene)
{
    scope.get<WorkBench>().givenSceneListToolHasBeenOpened()
        .selectScene(scene)
}

fun SceneSettingView.givenAvailableLocationsLoaded(): SceneSettingView
{
    if (driver().useLocationButton?.isShowing != true) {
        drive { useLocationButton!!.fire() }
    }
    return this
}

fun SceneSettingView.selectAvailableLocation(location: Location)
{
    drive {
        getAvailableLocationItem(location.id)!!.fire()
    }
}

fun SceneSettingView.removeLocation(location: Location)
{
    drive {
        getRemoveLocationButton(location.id)!!.fire()
    }
}