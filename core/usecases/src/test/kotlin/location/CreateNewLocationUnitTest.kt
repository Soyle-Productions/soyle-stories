package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.locationName
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocationUseCase
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CreateNewLocationUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val inputName = locationName()

	private var createdLocation: Location? = null
	private var result: Any? = null

	private val locationRepository = LocationRepositoryDouble(onAddNewLocation = ::createdLocation::set)

	@Test
	fun `valid name`() {
		createLocation()
		val result = result as CreateNewLocation.ResponseModel
		result.locationName.mustEqual(inputName.value)
	}

	@Test
	fun `valid locations are persisted`() {
		createLocation()
		assertLocationProperlyCreated()
	}

	@Test
	fun `description is optional`() {
		createLocation()
		assertLocationProperlyCreated()
		val createdLocation = createdLocation!!
		createdLocation.description.mustEqual("")
	}

	@Test
	fun `can provide description`() {
		val inputDescription = "I describe a location"
		createLocation(inputDescription = inputDescription)
		assertLocationProperlyCreated()
		val createdLocation = createdLocation!!
		createdLocation.description.mustEqual(inputDescription)
	}

	@Test
	fun `output generated id`() {
		createLocation()
		val createdLocation = createdLocation!!
		val result = result as CreateNewLocation.ResponseModel
		result.locationId.mustEqual(createdLocation.id.uuid)
	}

	private fun createLocation(inputName: SingleNonBlankLine = this.inputName, inputDescription: String? = null) {
		val useCase: CreateNewLocation = CreateNewLocationUseCase(projectId.uuid, locationRepository)
		runBlocking {
			useCase.invoke(inputName, inputDescription, object : CreateNewLocation.OutputPort {
				override fun receiveCreateNewLocationFailure(failure: Exception) {
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
		createdLocation.hostedScenes.isEmpty().mustEqual(true)
		createdLocation.name.mustEqual(inputName)
		createdLocation.projectId.mustEqual(projectId)
	}

}