package com.soyle.stories.repositories

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.repositories.LocationRepository

class LocationRepositoryImpl : LocationRepository {

	private val locations = mutableMapOf<Location.Id, Location>()

	override suspend fun addNewLocation(location: Location) {
		locations[location.id] = location
	}

	override suspend fun getAllLocationsInProject(projectId: Project.Id): List<Location> = locations.values.filter {
		it.projectId == projectId
	}

	override suspend fun getLocationById(locationId: Location.Id): Location? = locations[locationId]

	override suspend fun updateLocation(location: Location) {
		locations[location.id] = location
	}

	override suspend fun removeLocation(location: Location) {
		locations.remove(location.id)
	}

	override suspend fun getLocationIdsThatDoNotExist(locationIdsToTest: Set<Location.Id>): Set<Location.Id> {
		return locationIdsToTest.asSequence().filterNot { it in locations }.toSet()
	}
}