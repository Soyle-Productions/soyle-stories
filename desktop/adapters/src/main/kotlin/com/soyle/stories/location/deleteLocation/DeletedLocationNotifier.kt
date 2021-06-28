package com.soyle.stories.location.deleteLocation

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation

class DeletedLocationNotifier : Notifier<DeletedLocationReceiver>(), DeletedLocationReceiver {
    override suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation) {
        notifyAll { it.receiveDeletedLocation(deletedLocation) }
    }
}