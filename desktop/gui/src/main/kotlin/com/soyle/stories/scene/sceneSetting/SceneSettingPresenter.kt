package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.domain.scene.LocationRemovedFromScene
import com.soyle.stories.domain.scene.LocationUsedInScene
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.usecase.scene.locationsInScene.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.locationsInScene.listLocationsUsed.ListLocationsUsedInScene

internal class SceneSettingPresenter(
    private val view: View.Nullable<SceneSettingViewModel>,
    locationRemovedFromSceneNotifier: Notifier<LocationRemovedFromSceneReceiver>,
    locationUsedInSceneNotifier: Notifier<LocationUsedInSceneReceiver>
) : ListLocationsUsedInScene.OutputPort,
    ListAvailableLocationsToUseInScene.OutputPort,
    LocationRemovedFromSceneReceiver,
    LocationUsedInSceneReceiver {

    init {
        this listensTo locationRemovedFromSceneNotifier
        this listensTo locationUsedInSceneNotifier
    }

    override suspend fun receiveLocationsUsedInScene(response: ListLocationsUsedInScene.ResponseModel) {
        view.update {
            SceneSettingViewModel(
                targetSceneId = this?.targetSceneId,
                usedLocations = response.map {
                    LocationItemViewModel(it)
                },
                availableLocations = null
            )
        }
    }

    override suspend fun receiveAvailableLocationsToUseInScene(response: ListAvailableLocationsToUseInScene.ResponseModel) {
        view.updateOrInvalidated {
            copy(
                availableLocations = response.map {
                    LocationItemViewModel(it)
                }
            )
        }
    }

    override suspend fun receiveLocationRemovedFromScene(locationRemovedFromScene: LocationRemovedFromScene) {
        view.updateOrInvalidated {
            if (targetSceneId != locationRemovedFromScene.sceneId) return@updateOrInvalidated this
            copy(
                usedLocations = usedLocations.filterNot { it.id == locationRemovedFromScene.sceneSetting.id }
            )
        }
    }

    override suspend fun receiveLocationUsedInScene(locationUsedInScene: LocationUsedInScene) {
        view.updateOrInvalidated {
            if (targetSceneId != locationUsedInScene.sceneId) return@updateOrInvalidated this
            copy(
                usedLocations = usedLocations + LocationItemViewModel(
                    locationUsedInScene.sceneSetting.id,
                    locationUsedInScene.sceneSetting.locationName
                )
            )
        }
    }

}