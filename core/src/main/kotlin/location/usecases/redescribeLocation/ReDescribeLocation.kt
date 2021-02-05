package com.soyle.stories.location.usecases.redescribeLocation

import com.soyle.stories.location.LocationException
import java.util.*

interface ReDescribeLocation {

	suspend operator fun invoke(locationId: UUID, description: String, output: OutputPort)

	class ResponseModel(val locationId: UUID, val locationName: String, val updatedDescription: String)

	interface OutputPort {
		fun receiveReDescribeLocationFailure(failure: LocationException)
		fun receiveReDescribeLocationResponse(response: ResponseModel)
	}

}