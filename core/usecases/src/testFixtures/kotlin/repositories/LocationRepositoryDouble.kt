package com.soyle.stories.usecase.repositories

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.location.LocationRepository

class LocationRepositoryDouble(
	initialLocations: List<Location> = emptyList(),

	private val onAddNewLocation: (Location) -> Unit = {},
	private val onUpdateLocation: (Location) -> Unit = {},
	private val onRemoveLocation: (Location) -> Unit = {}
) : LocationRepository {

	val locations = initialLocations.associateBy { it.id }.toMutableMap()

	fun givenLocation(location: Location) {
		locations[location.id] = location
	}

	override suspend fun addNewLocation(location: Location) {
		onAddNewLocation.invoke(location)
		locations[location.id] = location
	}

	override suspend fun getLocationById(locationId: Location.Id): Location? = locations[locationId]
	override suspend fun getAllLocationsInProject(projectId: Project.Id): List<Location> {
		return locations.values.filter { it.projectId == projectId }
	}

	override suspend fun updateLocation(location: Location) {
		onUpdateLocation.invoke(location)
		locations[location.id] = location
	}

	override suspend fun removeLocation(location: Location) {
		onRemoveLocation.invoke(location)
		locations.remove(location.id)
	}

	override suspend fun getLocationIdsThatDoNotExist(locationIdsToTest: Set<Location.Id>): Set<Location.Id> {
		return locationIdsToTest.filterNot { it in locations }.toSet()
	}
}