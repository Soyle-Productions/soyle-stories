package com.soyle.stories.usecase.location.getLocationDetails

import java.util.*

interface GetLocationDetails {

	suspend operator fun invoke(locationId: UUID, output: OutputPort)

	class ResponseModel(val locationId: UUID, val locationName: String, val locationDescription: String)

	interface OutputPort {
		fun receiveGetLocationDetailsFailure(failure: Exception)
		fun receiveGetLocationDetailsResponse(response: ResponseModel)
	}

}