package com.soyle.stories.scene.setting.list

import com.soyle.stories.scene.setting.list.item.SceneSettingItemLocale
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButtonLocale
import javafx.beans.value.ObservableValue
import javafx.scene.Parent

interface SceneSettingItemListLocale {
    val useLocationsAsSceneSetting: ObservableValue<String>
    val noLocationUsedInSceneMessage: ObservableValue<Parent.() -> Unit>
    val sceneSettings: ObservableValue<String>
    val useLocation: ObservableValue<String>

    val failedToLoadUsedLocations: ObservableValue<String>
    val retry: ObservableValue<String>
}