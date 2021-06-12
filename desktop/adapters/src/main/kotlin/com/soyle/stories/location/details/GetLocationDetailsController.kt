package com.soyle.stories.location.details

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails

/*

By making this an interface and using an invoke method in the companion object, we allow it to be mocked in testing,
but we don't have to have an entirely separate file for a GetLocationDetailsControllerImpl.  The companion object
provides the default implementation class.

 */

interface GetLocationDetailsController {
    fun getLocationDetails(locationId: Location.Id, output: GetLocationDetails.OutputPort)

    companion object {
        operator fun invoke(
            threadTransformer: ThreadTransformer,
            getLocationDetails: GetLocationDetails
        ): GetLocationDetailsController = object : GetLocationDetailsController {
            override fun getLocationDetails(locationId: Location.Id, output: GetLocationDetails.OutputPort) {
                threadTransformer.async {
                    getLocationDetails.invoke(locationId.uuid, output)
                }
            }
        }
    }
}