package com.soyle.stories.location.usecases

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetails
import com.soyle.stories.location.usecases.getLocationDetails.GetLocationDetailsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class GetLocationDetailsUnitTest {

	val locationId = UUID.randomUUID()
	val locationName = "Location Name $locationId"
	val description = "I am a description"

	lateinit var locationRepository: LocationRepository

	var result: Any? = null

	@Test
	fun `location does not exist`() {
		givenNoLocations()
		whenUseCaseIsExecuted()
		val result = result as LocationDoesNotExist
		assertEquals(locationId, result.locationId)
	}

	@Test
	fun `location exists`() {
		given(locationsWithIdsOf = listOf(locationId))
		whenUseCaseIsExecuted()
		val result = result as GetLocationDetails.ResponseModel
		assertEquals(locationId, result.locationId)
		assertEquals(locationName, result.locationName)
		assertEquals(description, result.locationDescription)
	}

	private fun givenNoLocations() = given(emptyList())
	fun given(locationsWithIdsOf: List<UUID>)
	{
		locationRepository = LocationRepositoryDouble(
		  initialLocations = locationsWithIdsOf.map { Location(Location.Id(it), Project.Id(UUID.randomUUID()), locationName, description) }
		)
	}

	fun whenUseCaseIsExecuted() {
		val useCase: GetLocationDetails = GetLocationDetailsUseCase(locationRepository)
		runBlocking {
			useCase.invoke(locationId, object : GetLocationDetails.OutputPort {
				override fun receiveGetLocationDetailsFailure(failure: LocationException) {
					result = failure
				}
				override fun receiveGetLocationDetailsResponse(response: GetLocationDetails.ResponseModel) {
					result = response
				}
			})
		}
	}
}