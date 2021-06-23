package com.soyle.stories.scene.setting.list.useLocationButton

import javafx.beans.value.ObservableValue

interface UseLocationButtonLocale {
    val useLocation: ObservableValue<String>
    val loading: ObservableValue<String>
    val createLocation: ObservableValue<String>
    val allExistingLocationsInProjectHaveBeenUsed: ObservableValue<String>
}