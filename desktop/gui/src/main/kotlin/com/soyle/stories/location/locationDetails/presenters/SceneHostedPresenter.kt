package com.soyle.stories.location.locationDetails.presenters

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.location.hostedScene.SceneHostedReceiver
import com.soyle.stories.location.locationDetails.LocationDetailsViewModel
import com.soyle.stories.usecase.location.HostedSceneItem

class SceneHostedPresenter(
    private val locationId: Location.Id,
    private val view: LocationDetailsViewModel
) : SceneHostedReceiver {

    override suspend fun receiveSceneHostedAtLocation(event: SceneHostedAtLocation) {
        if (event.locationId != locationId) return
        with(view) {
            update {
                hostedScenes = hostedScenes + hostedSceneItemViewModel(event.sceneId, event.sceneName)
            }
        }
    }
}
