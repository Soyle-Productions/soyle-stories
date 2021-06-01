package com.soyle.stories.layout.doubles

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.repositories.LocationRepository

class LocationRepositoryDouble(
  initialLocations: List<Location> = emptyList(),

  private val onAddNewLocation: (Location) -> Unit = {},
  private val onUpdateLocation: (Location) -> Unit = {},
  private val onRemoveLocation: (Location) -> Unit = {}
) : LocationRepository {

	val locations = initialLocations.associateBy { it.id }.toMutableMap()

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
}