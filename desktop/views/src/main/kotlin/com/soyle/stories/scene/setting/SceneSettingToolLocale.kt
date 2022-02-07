package com.soyle.stories.scene.setting

import com.soyle.stories.common.markdown.ObservableMarkdownString
import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import javafx.beans.value.ObservableValue
import javafx.scene.Parent

interface SceneSettingToolLocale {
    val sceneSettingToolTitle: ObservableValue<String>
    val noSceneSelected: ObservableValue<String>
    val noSceneSelected_inviteMessage: ObservableMarkdownString
    fun selectedScene(sceneName: ObservableValue<String>): ObservableValue<String>
    val sceneSettingItemListLocale: SceneSettingItemListLocale

    val list: SceneSettingItemListLocale

}