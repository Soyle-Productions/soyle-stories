package com.soyle.stories.location.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.LocationNameCannotBeBlank
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocation
import com.soyle.stories.location.usecases.createNewLocation.CreateNewLocationUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class CreateNewLocationUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val inputName = "New Location"

	private var createdLocation: Location? = null
	private var result: Any? = null

	@BeforeEach
	fun clear() {
		createdLocation = null
		result = null
	}

	@ParameterizedTest
	@ValueSource(strings = ["", " ", "\r", "\n", "\r\n"])
	fun `blank names are invalid`(inputName: String) {
		whenExecutedWith(inputName)
		val result = result as LocationNameCannotBeBlank
	}

	@Test
	fun `valid name`() {
		whenExecuted()
		val result = result as CreateNewLocation.ResponseModel
		result.locationName.mustEqual(inputName)
	}

	@Test
	fun `valid locations are persisted`() {
		whenExecuted()
		assertLocationProperlyCreated()
	}

	@Test
	fun `description is optional`() {
		whenExecuted()
		assertLocationProperlyCreated()
		val createdLocation = createdLocation!!
		createdLocation.description.mustEqual("")
	}

	@Test
	fun `can provide description`() {
		val inputDescription = "I describe a location"
		whenExecutedWith(inputDescription = inputDescription)
		assertLocationProperlyCreated()
		val createdLocation = createdLocation!!
		createdLocation.description.mustEqual(inputDescription)
	}

	@Test
	fun `output generated id`() {
		whenExecuted()
		val createdLocation = createdLocation!!
		val result = result as CreateNewLocation.ResponseModel
		result.locationId.mustEqual(createdLocation.id.uuid)
	}

	private fun whenExecuted() = whenExecutedWith()
	private fun whenExecutedWith(inputName: String = this.inputName, inputDescription: String? = null) {
		val useCase: CreateNewLocation = CreateNewLocationUseCase(projectId.uuid, LocationRepositoryDouble(
		  onAddNewLocation = { createdLocation = it }
		))
		runBlocking {
			useCase.invoke(inputName, inputDescription, object : CreateNewLocation.OutputPort {
				override fun receiveCreateNewLocationFailure(failure: LocationException) {
					result = failure
				}

				override fun receiveCreateNewLocationResponse(response: CreateNewLocation.ResponseModel) {
					result = response
				}
			})
		}
	}

	private fun assertLocationProperlyCreated() {
		val createdLocation = createdLocation!!
		createdLocation.name.mustEqual(inputName)
		createdLocation.projectId.mustEqual(projectId)
	}

}