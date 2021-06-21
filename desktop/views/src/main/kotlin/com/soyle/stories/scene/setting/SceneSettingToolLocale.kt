package com.soyle.stories.scene.setting

import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import javafx.beans.value.ObservableValue
import javafx.scene.Parent

interface SceneSettingToolLocale {
    val sceneSettingToolTitle: ObservableValue<String>
    val noSceneSelected: ObservableValue<String>
    val useLocationsAsSceneSetting: ObservableValue<String>
    val noSceneSelectedInviteMessage: ObservableValue<Parent.() -> Unit>
    val selectedScene: ObservableValue<(String) -> String>
    val sceneSettingItemListLocale: SceneSettingItemListLocale
}