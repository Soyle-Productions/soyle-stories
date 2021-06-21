package com.soyle.stories.scene.setting

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.setting.list.SceneSettingItemListModel
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue

sealed class SceneSettingToolModel {
    object NoSceneSelected : SceneSettingToolModel()
    class SceneSelected(
        val sceneId: Scene.Id,
        val sceneName: StringProperty
    ) : SceneSettingToolModel()
}