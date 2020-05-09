package com.soyle.stories.location.usecases.redescribeLocation

import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.repositories.LocationRepository
import java.util.*

class ReDescribeLocationUseCase(
  private val locationRepository: LocationRepository
) : ReDescribeLocation {

	override suspend fun invoke(locationId: UUID, description: String, output: ReDescribeLocation.OutputPort) {
		val response = try {
			reDescribeLocation(locationId, description)
		} catch (l: LocationException) {
			return output.receiveReDescribeLocationFailure(l)
		}
		output.receiveReDescribeLocationResponse(response)
	}

	private suspend fun reDescribeLocation(locationId: UUID, description: String): ReDescribeLocation.ResponseModel
	{
		val location = getLocation(locationId)
		updateLocationIfNeeded(location, description)
		return ReDescribeLocation.ResponseModel(location.id.uuid, location.name, description)
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