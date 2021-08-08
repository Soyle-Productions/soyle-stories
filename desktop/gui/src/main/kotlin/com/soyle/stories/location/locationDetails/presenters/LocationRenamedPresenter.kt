package com.soyle.stories.location.locationDetails.presenters

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.LocationRenamed
import com.soyle.stories.location.locationDetails.LocationDetailsView
import com.soyle.stories.location.locationDetails.LocationDetailsViewModel
import com.soyle.stories.location.renameLocation.LocationRenamedReceiver

class LocationRenamedPresenter(
    private val locationId: Location.Id,
    private val view: LocationDetailsViewModel
) : LocationRenamedReceiver {
    override suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed) {
        with(view) {
            update {
                if (locationRenamed.locationId != locationId) return@update
                toolName = "Location Details - ${locationRenamed.newName}"
            }
        }
    }
}
