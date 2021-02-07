package com.soyle.stories.usecase.location

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocation
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocationUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class DeleteLocationUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val locationId: UUID = UUID.randomUUID()


	private val updatedCharacterArcs = mutableListOf<CharacterArc>()
	private var deletedLocation: Location? = null
	private var result: DeleteLocation.ResponseModel? = null

	private val locationRepository = LocationRepositoryDouble(onRemoveLocation = ::deletedLocation::set)
	private val characterArcSectionRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = updatedCharacterArcs::add)

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
		val arcs = List(3) {
			CharacterArc.planNewCharacterArc(Character.Id(), Theme.Id(), "")
				.withArcSection(makeCharacterArcSection(linkedLocation = Location.Id(locationId), template = Drive))
				.withArcSection(makeCharacterArcSection(linkedLocation = Location.Id(locationId), template = AttackByAlly))
				.withArcSection(makeCharacterArcSection(template = MoralDecision))
		}
		val arcSections = arcs.flatMap { it.arcSections }.filter { it.linkedLocation?.uuid == locationId }
		arcs.forEach(characterArcSectionRepository::givenCharacterArc)
		deleteLocation()
		val updatedCharacterArcSectionsById = arcSections.associateBy { it.id }
		arcSections.forEach { updatedCharacterArcSectionsById.getValue(it.id) }
		updatedCharacterArcs.forEach {
			assertTrue(it.arcSections.none { it.linkedLocation?.uuid == locationId })
		}
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