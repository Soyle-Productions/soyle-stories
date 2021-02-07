package com.soyle.stories.usecase.location.redescribeLocation

import java.util.*

interface ReDescribeLocation {

	suspend operator fun invoke(locationId: UUID, description: String, output: OutputPort)

	class ResponseModel(val locationId: UUID, val locationName: String, val updatedDescription: String)

	interface OutputPort {
		fun receiveReDescribeLocationFailure(failure: Exception)
		fun receiveReDescribeLocationResponse(response: ResponseModel)
	}

}