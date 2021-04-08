package com.soyle.stories.scene.sceneSetting

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import com.soyle.stories.domain.scene.events.LocationUsedInScene
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedReceiver
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneReceiver
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneReceiver
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene

internal class SceneSettingPresenter(
    private val view: View.Nullable<SceneSettingViewModel>,
    locationUsedInSceneNotifier: Notifier<LocationUsedInSceneReceiver>,
    sceneSettingLocationRenamed: Notifier<SceneSettingLocationRenamedReceiver>,
    locationRemovedFromScene: Notifier<LocationRemovedFromSceneReceiver>
) : ListLocationsUsedInScene.OutputPort,
    ListAvailableLocationsToUseInScene.OutputPort,
    LocationRemovedFromSceneReceiver,
    LocationUsedInSceneReceiver,
    SceneSettingLocationRenamedReceiver {

    init {
        this listensTo locationUsedInSceneNotifier
        this listensTo sceneSettingLocationRenamed
        this listensTo locationRemovedFromScene
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

    override suspend fun receiveSceneSettingLocationRenamed(sceneSettingLocationRenamed: SceneSettingLocationRenamed) {
        view.updateOrInvalidated {
            if (targetSceneId != sceneSettingLocationRenamed.sceneId) return@updateOrInvalidated this
            copy(
                usedLocations = usedLocations.map { item ->
                    if (item.id == sceneSettingLocationRenamed.sceneSettingLocation.id) {
                        item.copy(name = sceneSettingLocationRenamed.sceneSettingLocation.locationName)
                    } else {
                        item
                    }
                }
            )
        }
    }

}