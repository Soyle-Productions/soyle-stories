package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project


interface LocationRepository {
	suspend fun addNewLocation(location: Location)
	suspend fun getAllLocationsInProject(projectId: Project.Id): List<Location>
	suspend fun getLocationById(locationId: Location.Id): Location?
	suspend fun getLocationOrError(locationId: Location.Id) =
		getLocationById(locationId) ?: throw LocationDoesNotExist(locationId.uuid)
	suspend fun updateLocation(location: Location)
	suspend fun removeLocation(location: Location)

	suspend fun getLocationIdsThatDoNotExist(locationIdsToTest: Set<Location.Id>): Set<Location.Id>
}