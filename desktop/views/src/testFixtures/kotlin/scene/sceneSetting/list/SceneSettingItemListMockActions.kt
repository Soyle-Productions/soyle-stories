package com.soyle.stories.desktop.view.scene.sceneSetting.list

import com.soyle.stories.domain.location.Location
import com.soyle.stories.scene.setting.list.SceneSettingItemListActions

class SceneSettingItemListMockActions(
    private val onGetAvailableLocationsToUse: () -> Unit = {},
    private val onRemoveSceneSetting: (Location.Id) -> Unit = {},
    private val onRetryListUsedLocations: () -> Unit = {}
) : SceneSettingItemListActions {

    override fun getAvailableLocationsToUse() = onGetAvailableLocationsToUse()

    override fun removeSceneSetting(locationId: Location.Id) = onRemoveSceneSetting(locationId)

    override fun retryListUsedLocations() = onRetryListUsedLocations()
}