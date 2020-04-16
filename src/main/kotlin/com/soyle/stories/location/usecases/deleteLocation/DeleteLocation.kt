package com.soyle.stories.location.usecases.deleteLocation

import com.soyle.stories.location.LocationException
import java.util.*

interface DeleteLocation {
	suspend operator fun invoke(id: UUID, output: OutputPort)

	class ResponseModel(val locationId: UUID, val updatedArcSections: Set<UUID>)

	interface OutputPort {
		fun receiveDeleteLocationFailure(failure: LocationException)
		fun receiveDeleteLocationResponse(response: ResponseModel)
	}
}