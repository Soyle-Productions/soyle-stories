package com.soyle.stories.scene.setting.list

import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.useLocationButton.AvailableSceneSettingModel
import javafx.beans.binding.ListExpression
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.value.ObservableListValue
import javafx.collections.ObservableList

sealed class SceneSettingItemListModel {
    object Loading : SceneSettingItemListModel()
    object Error : SceneSettingItemListModel()
    class Loaded(
        val sceneSettings: ObservableList<SceneSettingItemModel>,
        val availableLocationsToUse: ListExpression<AvailableSceneSettingModel>
    ) : SceneSettingItemListModel()
}