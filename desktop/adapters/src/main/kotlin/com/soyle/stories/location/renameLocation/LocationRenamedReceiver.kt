package com.soyle.stories.location.renameLocation

import com.soyle.stories.entities.LocationRenamed

interface LocationRenamedReceiver {
    suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed)
}