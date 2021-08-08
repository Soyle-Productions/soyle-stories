package com.soyle.stories.scene.setting.list

import com.soyle.stories.domain.location.Location

interface SceneSettingItemListActions {
    fun getAvailableLocationsToUse()
    fun removeSceneSetting(locationId: Location.Id)
    fun retryListUsedLocations()
}