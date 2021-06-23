package com.soyle.stories.desktop.config.drivers.scene

import com.soyle.stories.common.components.text.FieldLabel.Companion.fieldLabel
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.config.drivers.soylestories.getWorkbenchForProjectOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.common.components.dataDisplay.`Chip Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.`Scene Setting Tool Root Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.list.`Scene Setting Item List Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.`Use Location Button Access`.Companion.access
import com.soyle.stories.di.get
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.setting.SceneSettingToolRoot
import com.soyle.stories.scene.setting.SceneSettingToolRoot.Styles.Companion.sceneSettingToolRoot
import javafx.event.ActionEvent
import tornadofx.FX

fun WorkBench.givenSceneSettingToolHasBeenOpened(): SceneSettingToolRoot =
    getOpenSceneSettingsTool() ?: openSceneSettingsTool().run { getOpenSceneSettingsToolOrError() }

fun WorkBench.getOpenSceneSettingsToolOrError(): SceneSettingToolRoot =
    getOpenSceneSettingsTool() ?: error("No Scene Setting tool is open in the project")

fun WorkBench.getOpenSceneSettingsTool(): SceneSettingToolRoot? {
    return robot.from(root).lookup(sceneSettingToolRoot.render()).queryAll<SceneSettingToolRoot>().firstOrNull()
        ?.takeIf { it.scene.window?.isShowing == true }
}

fun WorkBench.openSceneSettingsTool() {
    findMenuItemById("tools_scene setting")!!
        .apply { robot.interact { fire() } }
}

fun SceneSettingToolRoot.givenFocusedOn(scene: Scene): SceneSettingToolRoot {
    if (!access().isFocusedOn(scene)) focusOn(scene)
    return this
}

fun SceneSettingToolRoot.focusOn(scene: Scene) {
    soyleStories.getWorkbenchForProjectOrError(scene.projectId.uuid)
        .givenSceneListToolHasBeenOpened()
        .selectScene(scene)
}

fun SceneSettingToolRoot.givenAvailableLocationsLoaded(): SceneSettingToolRoot {
    access().list?.access {
        if (useLocationButton?.isShowing != true) {
            interact { useLocationButton!!.fire() }
        }
    }
    return this
}

fun SceneSettingToolRoot.selectAvailableLocation(location: Location) {
    access {
        val item = list?.access()?.useLocationButton?.access()?.availableLocationItem(location.id)
        interact { item!!.fire() }
    }
}

fun SceneSettingToolRoot.removeLocation(location: Location) {
    access {
        val button = list?.access()?.getSceneSettingItem(location.id)?.access()?.deleteButton
        interact { clickOn(button) }
    }
}
fun SceneSettingToolRoot.removeLocation(settingName: String) {
    access {
        val button = list?.access()?.getSceneSettingItemByName(settingName)?.access()?.deleteButton
        interact { clickOn(button) }
    }
}