package com.soyle.stories.location.usecases

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocation
import com.soyle.stories.location.usecases.redescribeLocation.ReDescribeLocationUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class ReDescribeLocationUnitTest {

	val locationId = UUID.randomUUID()
	val locationName = "Location Name"
	val initialDescription = "Original Description"

	val newDescription = "New Description"

	var updatedLocation: Location? = null
	var result: Any? = null

	@Test
	fun `location does not exist`() {
		givenNoLocations()
		whenUseCaseIsExecuted()
		val result = result as LocationDoesNotExist
		assertEquals(locationId, result.locationId, "Location id does not match input location id")
	}

	@Test
	fun `location exists`() {
		given(locationsWithIdsOf = listOf(locationId))
		whenUseCaseIsExecuted()
		assertIsValidResponseModel(result)
		assertIsValidStoredLocation(updatedLocation)
	}

	@Test
	fun `description is the same`() {
		given(locationsWithIdsOf = listOf(locationId), locationDescriptions = newDescription)
		whenUseCaseIsExecuted()
		assertIsValidResponseModel(result)
		assertNull(updatedLocation, "Should not save location if description has not been modified")
	}


	private fun assertIsValidResponseModel(actual: Any?)
	{
		actual as ReDescribeLocation.ResponseModel
		assertEquals(locationId, actual.locationId, "Location id does not match input location id")
		assertEquals(locationName, actual.locationName, "Location name does not match stored location name")
		assertEquals(newDescription, actual.updatedDescription, "Location description does not match input description")
	}

	private fun assertIsValidStoredLocation(actual: Any?)
	{
		actual as Location
		assertEquals(locationId, actual.id.uuid, "Incorrect location id was stored")
		assertEquals(locationName, actual.name, "Location name should not have been modified")
		assertEquals(newDescription, actual.description, "Location description not updated correctly")
	}


	private lateinit var locationRepo: LocationRepository

	private fun givenNoLocations() = given(emptyList())
	private fun given(locationsWithIdsOf: List<UUID>, locationDescriptions: String = initialDescription) {
		locationRepo = LocationRepositoryDouble(initialLocations = locationsWithIdsOf.map {
			Location(Location.Id(it), Project.Id(UUID.randomUUID()), locationName, locationDescriptions)
		}, onUpdateLocation = { updatedLocation = it })
	}
	private fun whenUseCaseIsExecuted() {
		val useCase: ReDescribeLocation = ReDescribeLocationUseCase(locationRepo)
		runBlocking {
			useCase.invoke(locationId, newDescription, object : ReDescribeLocation.OutputPort {
				override fun receiveReDescribeLocationFailure(failure: LocationException) {
					result = failure
				}

				override fun receiveReDescribeLocationResponse(response: ReDescribeLocation.ResponseModel) {
					result = response
				}
			})
		}
	}
}