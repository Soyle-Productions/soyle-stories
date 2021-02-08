package com.soyle.stories.location.deleteLocation

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocation

class DeleteLocationControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val deleteLocation: DeleteLocation,
    private val deleteLocationOutput: DeleteLocation.OutputPort
) : DeleteLocationController {

    override fun deleteLocation(locationId: Location.Id) {
        threadTransformer.async {
            deleteLocation.invoke(locationId.uuid, deleteLocationOutput)
        }
    }

}