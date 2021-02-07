package com.soyle.stories.usecase.location.redescribeLocation

import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.LocationRepository
import java.util.*

class ReDescribeLocationUseCase(
  private val locationRepository: LocationRepository
) : ReDescribeLocation {

	override suspend fun invoke(locationId: UUID, description: String, output: ReDescribeLocation.OutputPort) {
		val response = try {
			reDescribeLocation(locationId, description)
		} catch (l: Exception) {
			return output.receiveReDescribeLocationFailure(l)
		}
		output.receiveReDescribeLocationResponse(response)
	}

	private suspend fun reDescribeLocation(locationId: UUID, description: String): ReDescribeLocation.ResponseModel
	{
		val location = getLocation(locationId)
		updateLocationIfNeeded(location, description)
		return ReDescribeLocation.ResponseModel(location.id.uuid, location.name.value, description)
	}

	private suspend fun getLocation(locationId: UUID): Location
	{
		return locationRepository.getLocationById(Location.Id(locationId))
		  ?: throw LocationDoesNotExist(locationId)
	}

	private suspend fun updateLocationIfNeeded(location: Location, description: String) {
		if (location.description != description) {
			locationRepository.updateLocation(location.withDescription(description))
		}
	}
}