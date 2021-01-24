package com.soyle.stories.location.usecases

import com.soyle.stories.character.makeCharacterArcSection
import com.soyle.stories.common.mustEqual
import com.soyle.stories.doubles.CharacterArcSectionRepositoryDouble
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.makeLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocation
import com.soyle.stories.location.usecases.deleteLocation.DeleteLocationUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class DeleteLocationUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val locationId: UUID = UUID.randomUUID()


	private val updatedCharacterArcSections = mutableListOf<CharacterArcSection>()
	private var deletedLocation: Location? = null
	private var result: DeleteLocation.ResponseModel? = null

	private val locationRepository = LocationRepositoryDouble(onRemoveLocation = ::deletedLocation::set)
	private val characterArcSectionRepository = CharacterArcSectionRepositoryDouble(onUpdateCharacterArcSection = updatedCharacterArcSections::add)

	@Test
	fun `location does not exist`() {
		val error = assertThrows<LocationDoesNotExist> {
			deleteLocation()
		}
		error.locationId.mustEqual(locationId)
	}

	@Test
	fun `existing location is deleted`() {
		locationRepository.givenLocation(makeLocation(id = Location.Id(locationId), projectId = projectId))
		deleteLocation()
		val deletedLocation = deletedLocation!!
		deletedLocation.id.uuid.mustEqual(locationId)
		val result = result as DeleteLocation.ResponseModel
		result.deletedLocation.location.uuid.mustEqual(locationId)
	}

	@Test
	fun `update linked character arc sections`() {
		locationRepository.givenLocation(makeLocation(id = Location.Id(locationId), projectId = projectId))
		val arcSections = List(5) { makeCharacterArcSection(linkedLocation = Location.Id(locationId)) }
		arcSections.forEach(characterArcSectionRepository::givenCharacterArcSection)
		deleteLocation()
		val updatedCharacterArcSectionsById = updatedCharacterArcSections.associateBy { it.id }
		arcSections.forEach { updatedCharacterArcSectionsById.getValue(it.id) }
		updatedCharacterArcSections.forEach { it.linkedLocation.mustEqual(null) }
		val result = result as DeleteLocation.ResponseModel
		result.updatedArcSections.mustEqual(arcSections.map { it.id.uuid }.toSet())
	}

	private fun deleteLocation() {
		val useCase: DeleteLocation = DeleteLocationUseCase(locationRepository, characterArcSectionRepository)
		runBlocking {
			useCase.invoke(locationId, object : DeleteLocation.OutputPort {
				override suspend fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
					result = response
				}
			})
		}
	}

}