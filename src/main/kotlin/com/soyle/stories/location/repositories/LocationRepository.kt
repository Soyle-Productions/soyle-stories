package com.soyle.stories.location.repositories

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project

interface LocationRepository {
	suspend fun addNewLocation(location: Location)
	suspend fun getAllLocationsInProject(projectId: Project.Id): List<Location>
	suspend fun getLocationById(locationId: Location.Id): Location?
	suspend fun updateLocation(location: Location)
	suspend fun removeLocation(location: Location)

	suspend fun getLocationIdsThatDoNotExist(locationIdsToTest: Set<Location.Id>): Set<Location.Id>
}