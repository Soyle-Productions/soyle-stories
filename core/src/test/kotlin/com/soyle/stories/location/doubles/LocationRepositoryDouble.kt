package com.soyle.stories.location.doubles

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

	fun givenLocation(location: Location) {
		locations[location.id] = location
	}

	private val _persistedItems = mutableListOf<PersistenceLog>()
	val persistedItems: List<PersistenceLog>
		get() = _persistedItems

	private fun log(data: Any) {
		val type = Thread.currentThread().stackTrace.find {
			it.methodName != "log" && it.methodName != "getStackTrace"
		}?.methodName!!
		_persistedItems += PersistenceLog(type, data)
	}

	override suspend fun addNewLocation(location: Location) {
		log(location)
		onAddNewLocation.invoke(location)
		locations[location.id] = location
	}

	override suspend fun getLocationById(locationId: Location.Id): Location? = locations[locationId]
	override suspend fun getAllLocationsInProject(projectId: Project.Id): List<Location> {
		return locations.values.filter { it.projectId == projectId }
	}

	override suspend fun updateLocation(location: Location) {
		log(location)
		onUpdateLocation.invoke(location)
		locations[location.id] = location
	}

	override suspend fun removeLocation(location: Location) {
		log(location)
		onRemoveLocation.invoke(location)
		locations.remove(location.id)
	}

	override suspend fun getLocationIdsThatDoNotExist(locationIdsToTest: Set<Location.Id>): Set<Location.Id> {
		return locationIdsToTest.filterNot { it in locations }.toSet()
	}
}