package com.soyle.stories.location.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.*
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.CharacterArcSectionRepository
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocationUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class DeleteLocationUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val locationId: UUID = UUID.randomUUID()
	private val arcSectionIds = List(5) { UUID.randomUUID() }

	private lateinit var repository: LocationRepository
	private lateinit var characterArcSectionRepository: CharacterArcSectionRepository

	private var updatedCharacterArcSections: List<CharacterArcSection> = emptyList()
	private var deletedLocation: Location? = null
	private var result: Any? = null

	@BeforeEach
	fun clear() {
		result = null
		deletedLocation = null
		updatedCharacterArcSections = emptyList()
	}

	@Test
	fun `location does not exist`() {
		givenNoLocations()
		whenUseCaseIsExecuted()
		val result = result as LocationDoesNotExist
		result.locationId.mustEqual(locationId)
	}

	@Test
	fun `existing location is deleted`() {
		given(locationWithId = locationId)
		whenUseCaseIsExecuted()
		val deletedLocation = deletedLocation!!
		deletedLocation.id.uuid.mustEqual(locationId)
		val result = result as DeleteLocation.ResponseModel
		result.locationId.mustEqual(locationId)
	}

	@Test
	fun `update linked character arc sections`() {
		given(locationWithId = locationId, linkedArcSectionIds = arcSectionIds)
		whenUseCaseIsExecuted()
		val updatedCharacterArcSections = updatedCharacterArcSections.associateBy { it.id.uuid }
		arcSectionIds.forEach { updatedCharacterArcSections.getValue(it) }
		updatedCharacterArcSections.values.forEach { it.linkedLocation.mustEqual(null) }
		val result = result as DeleteLocation.ResponseModel
		result.updatedArcSections.mustEqual(arcSectionIds.toSet())
	}

	private fun givenNoLocations() = given(null)
	private fun given(locationWithId: UUID? = null, linkedArcSectionIds: List<UUID> = emptyList()) {
		repository = LocationRepositoryDouble(
		  initialLocations = listOfNotNull(
			locationWithId?.let { Location(Location.Id(it), projectId, "Location Name") }
		  ),
		  onRemoveLocation = {
			  deletedLocation = it
		  }
		)
		characterArcSectionRepository = object : CharacterArcSectionRepository {
			override fun getCharacterArcSectionsLinkedToLocation(locationId: Location.Id): List<CharacterArcSection> {
				return linkedArcSectionIds.map {
					CharacterArcSection(CharacterArcSection.Id(it), Character.Id(UUID.randomUUID()), Theme.Id(UUID.randomUUID()), CharacterArcTemplate.default().sections.first(), locationId,"")
				}
			}

			override fun updateCharacterArcSections(characterArcSections: Set<CharacterArcSection>) {
				updatedCharacterArcSections = characterArcSections.toList()
			}
		}
	}

	private fun whenUseCaseIsExecuted() {
		val useCase: DeleteLocation = DeleteLocationUseCase(repository, characterArcSectionRepository)
		runBlocking {
			useCase.invoke(locationId, object : DeleteLocation.OutputPort {
				override fun receiveDeleteLocationFailure(failure: LocationException) {
					result = failure
				}

				override fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
					result = response
				}
			})
		}
	}

}