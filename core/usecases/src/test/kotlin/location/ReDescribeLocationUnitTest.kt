package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.locationName
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocation
import com.soyle.stories.usecase.location.redescribeLocation.ReDescribeLocationUseCase
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class ReDescribeLocationUnitTest {

	val locationId = UUID.randomUUID()
	val locationName = locationName()
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
		assertEquals(locationName.value, actual.locationName, "Location name does not match stored location name")
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
			makeLocation(
				id = Location.Id(it),
				name = locationName,
				description = locationDescriptions
			)
		}, onUpdateLocation = { updatedLocation = it })
	}
	private fun whenUseCaseIsExecuted() {
		val useCase: ReDescribeLocation = ReDescribeLocationUseCase(locationRepo)
		runBlocking {
			useCase.invoke(locationId, newDescription, object : ReDescribeLocation.OutputPort {
				override fun receiveReDescribeLocationFailure(failure: Exception) {
					result = failure
				}

				override fun receiveReDescribeLocationResponse(response: ReDescribeLocation.ResponseModel) {
					result = response
				}
			})
		}
	}
}