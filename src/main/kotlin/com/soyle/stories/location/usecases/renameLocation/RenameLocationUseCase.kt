package com.soyle.stories.location.usecases.renameLocation

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.LocationRenamed
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.repositories.getLocationOrError

class RenameLocationUseCase(
  private val locationRepository: LocationRepository
) : RenameLocation {

	override suspend fun invoke(id: Location.Id, name: SingleNonBlankLine, output: RenameLocation.OutputPort) {
		val location = locationRepository.getLocationOrError(id)
		val locationRenamed = updateIfNamesAreDifferent(name, location) ?: return
		output.receiveRenameLocationResponse(RenameLocation.ResponseModel(locationRenamed))
	}

	private suspend fun updateIfNamesAreDifferent(name: SingleNonBlankLine, location: Location): LocationRenamed? {
		return if (name != location.name) updateLocationName(location, name)
		else null
	}

	private suspend fun updateLocationName(location: Location, name: SingleNonBlankLine): LocationRenamed {
		locationRepository.updateLocation(location.withName(name))
		return LocationRenamed(location.id, name.value)
	}
}