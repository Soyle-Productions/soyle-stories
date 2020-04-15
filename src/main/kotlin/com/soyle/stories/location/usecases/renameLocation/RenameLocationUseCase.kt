package com.soyle.stories.location.usecases.renameLocation

import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.validateLocationName
import java.util.*

class RenameLocationUseCase(
  private val locationRepository: LocationRepository
) : RenameLocation {
	override suspend fun invoke(id: UUID, name: String, output: RenameLocation.OutputPort) {
		val response = try {
			renameLocation(name, id)
		} catch (l: LocationException) {
			return output.receiveRenameLocationFailure(l)
		}
		output.receiveRenameLocationResponse(response)
	}

	private suspend fun renameLocation(name: String, id: UUID): RenameLocation.ResponseModel {
		validateLocationName(name)
		val location = getLocationOrFail(id)
		updateIfNamesAreDifferent(name, location)
		return RenameLocation.ResponseModel(id, name)
	}

	private suspend fun updateIfNamesAreDifferent(name: String, location: Location) {
		if (name != location.name) updateLocationName(location, name)
	}

	private suspend fun updateLocationName(location: Location, name: String) {
		locationRepository.updateLocation(location.withName(name))
	}

	private suspend fun getLocationOrFail(id: UUID): Location {
		return locationRepository.getLocationById(Location.Id(id))
		  ?: throw LocationDoesNotExist(id)
	}
}