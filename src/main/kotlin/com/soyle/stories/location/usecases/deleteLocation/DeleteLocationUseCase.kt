package com.soyle.stories.location.usecases.deleteLocation

import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.repositories.CharacterArcSectionRepository
import com.soyle.stories.location.repositories.LocationRepository
import java.util.*

class DeleteLocationUseCase(
  private val locationRepository: LocationRepository,
  private val characterArcSectionRepository: CharacterArcSectionRepository
) : DeleteLocation {
	override suspend fun invoke(id: UUID, output: DeleteLocation.OutputPort) {
		val response = try {
			deleteLocation(id)
		} catch (l: LocationException) {
			return output.receiveDeleteLocationFailure(l)
		}
		output.receiveDeleteLocationResponse(response)
	}

	private suspend fun deleteLocation(id: UUID): DeleteLocation.ResponseModel {
		val location = getLocationOrFail(id)
		val arcSections = updateLinkedArcSections(location)
		locationRepository.removeLocation(location)
		return DeleteLocation.ResponseModel(id, arcSections.map { it.id.uuid }.toSet())
	}

	private fun updateLinkedArcSections(location: Location): List<CharacterArcSection> {
		val arcSections = characterArcSectionRepository.getCharacterArcSectionsLinkedToLocation(location.id)
		  .map { it.withoutLinkedLocation() }
		characterArcSectionRepository.updateCharacterArcSections(arcSections.toSet())
		return arcSections
	}

	private suspend fun getLocationOrFail(id: UUID): Location {
		return locationRepository.getLocationById(Location.Id(id))
		  ?: throw LocationDoesNotExist(id)
	}
}