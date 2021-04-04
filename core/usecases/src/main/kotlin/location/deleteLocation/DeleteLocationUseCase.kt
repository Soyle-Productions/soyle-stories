package com.soyle.stories.usecase.location.deleteLocation

import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.scene.SceneRepository
import java.util.*

class DeleteLocationUseCase(
	private val locationRepository: LocationRepository,
	private val characterArcRepository: CharacterArcRepository,
	private val sceneRepository: SceneRepository
) : DeleteLocation {
	override suspend fun invoke(id: UUID, output: DeleteLocation.OutputPort) {
		output.receiveDeleteLocationResponse(deleteLocation(id))
	}

	private suspend fun deleteLocation(id: UUID): DeleteLocation.ResponseModel {
		val location = getLocationOrFail(id)
		val arcSections = updateLinkedArcSections(location)
		val sceneUpdates = sceneRepository.getScenesUsingLocation(location.id)
			.mapNotNull {
				it.withoutLocation(location.id) as? Updated
			}
		if (sceneUpdates.isNotEmpty()) {
			sceneRepository.updateScenes(sceneUpdates.map { it.scene })
		}
		locationRepository.removeLocation(location)
		val deletedLocation = DeletedLocation(location.id)
		return DeleteLocation.ResponseModel(deletedLocation, arcSections.map { it.id.uuid }.toSet(),
			sceneUpdates.map { it.event })
	}

	private suspend fun updateLinkedArcSections(location: Location): List<CharacterArcSection> {
		val arcsWithLinkedSections = characterArcRepository.getCharacterArcsWithSectionsLinkedToLocation(location.id)
		val arcSectionsWithLinkedLocation = arcsWithLinkedSections.asSequence()
			.flatMap { it.arcSections.asSequence() }
			.filter { it.linkedLocation == location.id }
			.toList()
		val updatedArcs = arcsWithLinkedSections.map { arc ->
			arc.withArcSectionsMapped {
				if (it.linkedLocation == location.id) {
					it.withoutLinkedLocation()
				} else it
			}
		}
		characterArcRepository.updateCharacterArcs(updatedArcs.toSet())
		return arcSectionsWithLinkedLocation
	}

	private suspend fun getLocationOrFail(id: UUID): Location {
		return locationRepository.getLocationById(Location.Id(id))
		  ?: throw LocationDoesNotExist(id)
	}
}