package com.soyle.stories.location.renameLocation

import com.soyle.stories.domain.location.events.LocationRenamed

fun interface LocationRenamedReceiver {
    suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed)
}