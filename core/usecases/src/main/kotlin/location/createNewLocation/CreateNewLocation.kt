package com.soyle.stories.usecase.location.createNewLocation

import com.soyle.stories.domain.validation.SingleNonBlankLine
import java.util.*

interface CreateNewLocation {
	suspend operator fun invoke(name: SingleNonBlankLine, output: OutputPort) = invoke(name, null, output)
	suspend operator fun invoke(name: SingleNonBlankLine, description: String?, output: OutputPort)

	class ResponseModel(val locationId: UUID, val locationName: String)

	interface OutputPort {
		fun receiveCreateNewLocationFailure(failure: Exception)
		fun receiveCreateNewLocationResponse(response: ResponseModel)
	}
}