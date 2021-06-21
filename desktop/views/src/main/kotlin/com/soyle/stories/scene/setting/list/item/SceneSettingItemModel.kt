package com.soyle.stories.scene.setting.list.item

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import javafx.beans.value.ObservableValue

class SceneSettingItemModel(
    val sceneId: Scene.Id,
    val locationId: Location.Id,
    val locationName: String,
    val removed: ObservableValue<Boolean>
)