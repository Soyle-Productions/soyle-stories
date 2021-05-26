package com.soyle.stories.location.renameLocation

import com.soyle.stories.domain.location.events.LocationRenamed

interface LocationRenamedReceiver {
    suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed)
}