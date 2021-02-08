package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.location.events.LocationEvents
import com.soyle.stories.location.locationDetails.presenters.LocationRenamedPresenter
import com.soyle.stories.location.locationDetails.presenters.ReDescribeLocationPresenter
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails

class LocationDetailsPresenter(
    locationId: String,
    private val view: LocationDetailsView,
    locationEvents: LocationEvents,
    locationRenamedNotifier: Notifier<LocationRenamedReceiver>
) : GetLocationDetails.OutputPort {

    private val subPresenters = listOf(
        LocationRenamedPresenter(locationId, view) listensTo locationRenamedNotifier,
        ReDescribeLocationPresenter(locationId, view) listensTo locationEvents.reDescribeLocation
    )


    override fun receiveGetLocationDetailsResponse(response: GetLocationDetails.ResponseModel) {
        view.update {
            LocationDetailsViewModel(
                toolName = "Location Details - ${response.locationName}",
                descriptionLabel = "Description",
                description = response.locationDescription
            )
        }
    }

    override fun receiveGetLocationDetailsFailure(failure: Exception) {

    }
}