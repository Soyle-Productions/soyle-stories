package com.soyle.stories.scene.setting.list.useLocationButton

import com.soyle.stories.domain.location.Location
import javafx.beans.value.ObservableValue

class AvailableSceneSettingModel(
    val locationId: Location.Id,
    val locationName: ObservableValue<String>
)