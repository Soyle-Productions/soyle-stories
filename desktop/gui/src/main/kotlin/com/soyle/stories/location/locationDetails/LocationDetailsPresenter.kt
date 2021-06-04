package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.locationDetails.presenters.*
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails
import com.soyle.stories.usecase.location.hostedScene.listAvailableScenes.ListScenesToHostInLocation
import java.util.*

class LocationDetailsPresenter(
    private val locationId: Location.Id,
    private val view: LocationDetailsViewModel,
    locationEvents: LocationEvents,
) : GetLocationDetails.OutputPort, ListScenesToHostInLocation.OutputPort {

    private val subPresenters = listOf(
        LocationRenamedPresenter(locationId, view) listensTo locationEvents.locationRenamed,
        ReDescribeLocationPresenter(locationId, view) listensTo locationEvents.reDescribeLocation,
        SceneHostedPresenter(locationId, view) listensTo locationEvents.sceneHosted,
        HostedSceneRenamedPresenter(locationId, view) listensTo locationEvents.hostedSceneRenamed,
        HostedSceneRemovedPresenter(locationId, view) listensTo locationEvents.hostedSceneRemoved,
    )

    override suspend fun receiveGetLocationDetailsResponse(response: GetLocationDetails.ResponseModel) {
        with(view) {
            update {
                toolName = "Location Details - ${response.locationName}"
                descriptionLabel = "Description"
                description = response.locationDescription
                hostedScenes = response.hostedScenes.map { hostedSceneItemViewModel(it.sceneId, it.sceneName) }
            }
        }
    }

    override suspend fun receiveScenesAvailableToHostInLocation(response: ListScenesToHostInLocation.ResponseModel) {
        if (locationId != response.locationId) return
        with(view) {
            update {
                availableScenesToHost = response.availableScenesToHost.map {
                    AvailableSceneToHostViewModel(it.sceneId, it.sceneName)
                }
            }
        }
    }
}
