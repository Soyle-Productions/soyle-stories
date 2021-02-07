package com.soyle.stories.usecase.location.getLocationDetails

import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.LocationRepository
import java.util.*

class GetLocationDetailsUseCase(
  private val locationRepository: LocationRepository
) : GetLocationDetails {

	override suspend fun invoke(locationId: UUID, output: GetLocationDetails.OutputPort) {
		val location = locationRepository.getLocationById(Location.Id(locationId))
		  ?: return output.receiveGetLocationDetailsFailure(LocationDoesNotExist(locationId))

		output.receiveGetLocationDetailsResponse(GetLocationDetails.ResponseModel(locationId, location.name.value, location.description))
	}
}