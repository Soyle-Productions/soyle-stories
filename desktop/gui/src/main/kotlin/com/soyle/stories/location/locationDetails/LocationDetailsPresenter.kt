package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.locationDetails.presenters.*
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails
import java.util.*

class LocationDetailsPresenter(
    locationId: String,
    private val view: LocationDetailsViewModel,
    locationEvents: LocationEvents,
    locationRenamedNotifier: Notifier<LocationRenamedReceiver>
) : GetLocationDetails.OutputPort {

    private val subPresenters = listOf(
        LocationRenamedPresenter(locationId, view) listensTo locationRenamedNotifier,
        ReDescribeLocationPresenter(locationId, view) listensTo locationEvents.reDescribeLocation,
        SceneHostedPresenter(Location.Id(UUID.fromString(locationId)), view) listensTo locationEvents.sceneHosted,
        HostedSceneRenamedPresenter(Location.Id(UUID.fromString(locationId)), view) listensTo locationEvents.hostedSceneRenamed,
        HostedSceneRemovedPresenter(Location.Id(UUID.fromString(locationId)), view) listensTo locationEvents.hostedSceneRemoved,
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
}
