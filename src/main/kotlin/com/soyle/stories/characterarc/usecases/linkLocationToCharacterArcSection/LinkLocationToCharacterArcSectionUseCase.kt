package com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.theme.repositories.CharacterArcRepository
import java.util.*

class LinkLocationToCharacterArcSectionUseCase(
	private val characterArcRepository: CharacterArcRepository,
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
		val characterArc = getCharacterArc(characterArcSectionId)
		val location = getLocation(locationId)
		updateCharacterArcSectionIfNeeded(characterArc, CharacterArcSection.Id(characterArcSectionId), location)
		return LinkLocationToCharacterArcSection.ResponseModel(characterArcSectionId, locationId)
	}

	private suspend fun updateCharacterArcSectionIfNeeded(characterArc: CharacterArc, characterArcSectionId: CharacterArcSection.Id, location: Location) {
		val characterArcSection = characterArc.arcSections.find { it.id == characterArcSectionId }!!
		if (characterArcSection.linkedLocation != location.id) {
			characterArcRepository.replaceCharacterArcs(
				characterArc.withArcSectionsMapped {
					if (it.id == characterArcSectionId) it.withLinkedLocation(location.id)
					else it
				}
			)
		}
	}

	private suspend fun getLocation(locationId: UUID) =
	  (locationRepository.getLocationById(Location.Id(locationId))
		?: throw LocationDoesNotExist(locationId))

	private suspend fun getCharacterArc(characterArcSectionId: UUID): CharacterArc {
		return (characterArcRepository
		  .getCharacterArcContainingArcSection(CharacterArcSection.Id(characterArcSectionId))
		  ?: throw CharacterArcSectionDoesNotExist(characterArcSectionId))
	}
}