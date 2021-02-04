package com.soyle.stories.location.usecases.deleteLocation

import java.util.*

interface DeleteLocation {
	suspend operator fun invoke(id: UUID, output: OutputPort)

	class ResponseModel(
		val deletedLocation: DeletedLocation,
		val updatedArcSections: Set<UUID>
		)

	interface OutputPort {
		suspend fun receiveDeleteLocationResponse(response: ResponseModel)
	}
}