package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocations
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocationsUseCase
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
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
			makeLocation(id = Location.Id(it), projectId = projectId)
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
		  .mustEqual(storedLocations.map{ it.name.value }.toSet()) { "Location names do not match" }
	}
}