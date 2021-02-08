package com.soyle.stories.location.deleteLocation

import com.soyle.stories.usecase.location.deleteLocation.DeleteLocation

class DeleteLocationOutput(
    private val deletedLocationReceiver: DeletedLocationReceiver
) : DeleteLocation.OutputPort {

    override suspend fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
        deletedLocationReceiver.receiveDeletedLocation(response.deletedLocation)
    }

}