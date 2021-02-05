package com.soyle.stories.location.usecases.createNewLocation

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.location.LocationException
import java.util.*

interface CreateNewLocation {
	suspend operator fun invoke(name: SingleNonBlankLine, output: OutputPort) = invoke(name, null, output)
	suspend operator fun invoke(name: SingleNonBlankLine, description: String?, output: OutputPort)

	class ResponseModel(val locationId: UUID, val locationName: String)

	interface OutputPort {
		fun receiveCreateNewLocationFailure(failure: LocationException)
		fun receiveCreateNewLocationResponse(response: ResponseModel)
	}
}