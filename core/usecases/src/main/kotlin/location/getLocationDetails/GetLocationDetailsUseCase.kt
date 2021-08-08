package com.soyle.stories.usecase.location.getLocationDetails

import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.HostedSceneItem
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.LocationRepository
import java.util.*

class GetLocationDetailsUseCase(
	private val locationRepository: LocationRepository
) : GetLocationDetails {

	override suspend fun invoke(locationId: UUID, output: GetLocationDetails.OutputPort) {
		val location = locationRepository.getLocationOrError(Location.Id(locationId))

		val response = GetLocationDetails.ResponseModel(
			locationId,
			location.name.value,
			location.description,
			location.hostedScenes.map { HostedSceneItem(it.id, it.sceneName) }
		)
		output.receiveGetLocationDetailsResponse(response)
	}
}
