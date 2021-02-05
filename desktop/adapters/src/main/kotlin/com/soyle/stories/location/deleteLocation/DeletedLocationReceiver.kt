package com.soyle.stories.location.deleteLocation

import com.soyle.stories.location.usecases.deleteLocation.DeletedLocation

interface DeletedLocationReceiver {
    suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation)
}