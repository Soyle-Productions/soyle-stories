package com.soyle.stories.location.renameLocation

import com.soyle.stories.common.Notifier
import com.soyle.stories.entities.LocationRenamed

class LocationRenamedNotifier : Notifier<LocationRenamedReceiver>(), LocationRenamedReceiver {
    override suspend fun receiveLocationRenamed(locationRenamed: LocationRenamed) {
        notifyAll { it.receiveLocationRenamed(locationRenamed) }
    }
}