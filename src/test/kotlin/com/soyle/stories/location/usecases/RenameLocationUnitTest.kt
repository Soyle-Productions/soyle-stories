package com.soyle.stories.location.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.LocationNameCannotBeBlank
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.renameLocation.RenameLocation
import com.soyle.stories.location.usecases.renameLocation.RenameLocationUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class RenameLocationUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val locationId: UUID = UUID.randomUUID()
	private val originalName: String = "First Location Name"
	private val inputName: String = "Location Name"

	private lateinit var locationRepository: LocationRepository

	private var updatedLocation: Location? = null
	private var result: Any? = null

	@BeforeEach
	fun clear() {
		locationRepository = LocationRepositoryDouble()
		updatedLocation = null
		result = null
	}

	@ParameterizedTest
	@ValueSource(strings = ["", " ", "\r", "\n", "\r\n"])
	fun `new name is blank`(inputName: String) {
		givenNoLocations()
		whenUseCaseIsExecuted(withName = inputName)
		val result = result as LocationNameCannotBeBlank
	}

	@Test
	fun `location does not exist`() {
		givenNoLocations()
		whenUseCaseIsExecuted()
		val result = result as LocationDoesNotExist
		result.locationId.mustEqual(locationId)
	}

	@Test
	fun `valid name is output`() {
		given(locationWithId = locationId)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
	}

	@Test
	fun `same name is not persisted`() {
		given(locationWithId = locationId, andName = inputName)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
		assertLocationNotUpdated()
	}

	@Test
	fun `modified valid name is persisted`() {
		given(locationWithId = locationId)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
		assertOnlyLocationNameUpdated()
	}

	private fun givenNoLocations() = given()
	private fun given(locationWithId: UUID? = null, andName: String? = null) {
		locationRepository = LocationRepositoryDouble(
		  initialLocations = listOfNotNull(
			locationWithId?.let { Location(Location.Id(it), projectId, andName ?: originalName) }
		  ),
		  onUpdateLocation = { updatedLocation = it }
		)
	}

	private fun whenUseCaseIsExecuted(withName: String = inputName) {
		val useCase: RenameLocation = RenameLocationUseCase(locationRepository)
		runBlocking {
			useCase.invoke(locationId, withName, object : RenameLocation.OutputPort {
				override fun receiveRenameLocationFailure(failure: LocationException) {
					result = failure
				}

				override fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
					result = response
				}
			})
		}
	}

	private fun assertResultIsValidResponseModel() {
		val result = result as RenameLocation.ResponseModel
		result.locationId.mustEqual(locationId)
		result.newName.mustEqual(inputName)
	}

	private fun assertLocationNotUpdated() {
		val updatedLocation = updatedLocation
		updatedLocation.mustEqual(null) { "Location should not have been updated" }
	}

	private fun assertOnlyLocationNameUpdated() {
		val updatedLocation = updatedLocation!!
		updatedLocation.name.mustEqual(inputName)
	}

}