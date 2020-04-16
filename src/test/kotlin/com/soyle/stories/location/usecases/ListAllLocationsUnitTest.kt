package com.soyle.stories.location.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocationsUseCase
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ListAllLocationsUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val locationIds: List<UUID> = List(5) { UUID.randomUUID() }

	private var storedLocations: List<Location> = emptyList()
	private lateinit var locationRepository: LocationRepository

	private var result: Any? = null

	@BeforeEach
	fun clear() {
		storedLocations = emptyList()
		locationRepository = LocationRepositoryDouble()
		result = null
	}

	@Test
	fun `no locations`() {
		givenNoLocations()
		whenUseCaseIsExecuted()
		assertOutputIsEmpty()
	}

	@Test
	fun `some locations`() {
		given(locationIds = locationIds)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
	}

	private fun givenNoLocations() = given()
	private fun given(locationIds: List<UUID> = emptyList()) {
		storedLocations = locationIds.map {
			Location(Location.Id(it), projectId, "Unique Location Name: $it")
		}
		locationRepository = LocationRepositoryDouble(
		  initialLocations = storedLocations
		)
	}

	private fun whenUseCaseIsExecuted() {
		val useCase: ListAllLocations = ListAllLocationsUseCase(projectId.uuid, locationRepository)
		runBlocking {
			useCase.invoke(object : ListAllLocations.OutputPort {
				override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
					result = response
				}
			})
		}
	}
	private fun assertOutputIsEmpty() {
		val result = result as ListAllLocations.ResponseModel
		result.locations.isEmpty().mustEqual(true) { "Output locations is not empty" }
	}
	private fun assertResultIsValidResponseModel() {
		val result = result as ListAllLocations.ResponseModel
		result.locations.map(LocationItem::id).toSet().mustEqual(locationIds.toSet()) { "Not all locations output" }
		result.locations.map(LocationItem::locationName).toSet()
		  .mustEqual(storedLocations.map(Location::name).toSet()) { "Location names do not match" }
	}
}