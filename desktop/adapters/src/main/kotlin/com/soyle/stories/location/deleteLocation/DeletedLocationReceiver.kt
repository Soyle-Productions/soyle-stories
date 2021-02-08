package com.soyle.stories.location.deleteLocation

import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation

interface DeletedLocationReceiver {
    suspend fun receiveDeletedLocation(deletedLocation: DeletedLocation)
}