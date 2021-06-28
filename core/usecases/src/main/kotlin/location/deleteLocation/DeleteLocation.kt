package com.soyle.stories.usecase.location.deleteLocation

import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import java.util.*

interface DeleteLocation {
	suspend operator fun invoke(id: UUID, output: OutputPort)

	class ResponseModel(
		val deletedLocation: DeletedLocation,
		val updatedArcSections: Set<UUID>,
		val locationRemovedFromScenes: List<LocationRemovedFromScene>
		)

	interface OutputPort {
		suspend fun receiveDeleteLocationResponse(response: ResponseModel)
	}
}