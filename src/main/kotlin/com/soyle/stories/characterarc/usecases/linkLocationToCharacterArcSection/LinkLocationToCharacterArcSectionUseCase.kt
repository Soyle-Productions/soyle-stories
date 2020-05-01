package com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import java.util.*

class LinkLocationToCharacterArcSectionUseCase(
  private val characterArcSectionRepository: CharacterArcSectionRepository,
  private val locationRepository: LocationRepository
) : LinkLocationToCharacterArcSection {
	override suspend fun invoke(characterArcSectionId: UUID, locationId: UUID, output: LinkLocationToCharacterArcSection.OutputPort) {
		val response = try {
			linkLocationToCharacterArcSection(characterArcSectionId, locationId)
		} catch (e: Exception) {
			return output.receiveLinkLocationToCharacterArcSectionFailure(e)
		}
		output.receiveLinkLocationToCharacterArcSectionResponse(response)
	}

	private suspend fun linkLocationToCharacterArcSection(characterArcSectionId: UUID, locationId: UUID): LinkLocationToCharacterArcSection.ResponseModel
	{
		val characterArcSection = getCharacterArcSection(characterArcSectionId)
		val location = getLocation(locationId)
		updateCharacterArcSectionIfNeeded(characterArcSection, location)
		return LinkLocationToCharacterArcSection.ResponseModel(characterArcSectionId, locationId)
	}

	private suspend fun updateCharacterArcSectionIfNeeded(characterArcSection: CharacterArcSection, location: Location) {
		if (characterArcSection.linkedLocation != location.id) {
			characterArcSectionRepository.updateCharacterArcSection(characterArcSection.withLinkedLocation(location.id))
		}
	}

	private suspend fun getLocation(locationId: UUID) =
	  (locationRepository.getLocationById(Location.Id(locationId))
		?: throw LocationDoesNotExist(locationId))

	private suspend fun getCharacterArcSection(characterArcSectionId: UUID): CharacterArcSection {
		return (characterArcSectionRepository
		  .getCharacterArcSectionById(CharacterArcSection.Id(characterArcSectionId))
		  ?: throw CharacterArcSectionDoesNotExist(characterArcSectionId))
	}
}