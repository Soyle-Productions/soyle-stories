package com.soyle.stories.location.usecases.createNewLocation

import com.soyle.stories.location.LocationException
import java.util.*

interface CreateNewLocation {
	suspend operator fun invoke(name: String, output: OutputPort) = invoke(name, null, output)
	suspend operator fun invoke(name: String, description: String?, output: OutputPort)

	class ResponseModel(val locationId: UUID, val locationName: String)

	interface OutputPort {
		fun receiveCreateNewLocationFailure(failure: LocationException)
		fun receiveCreateNewLocationResponse(response: ResponseModel)
	}
}