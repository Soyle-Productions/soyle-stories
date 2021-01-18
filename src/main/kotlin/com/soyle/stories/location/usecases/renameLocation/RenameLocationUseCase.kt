package com.soyle.stories.location.usecases.renameLocation

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.LocationRenamed
import com.soyle.stories.entities.mentioned
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.location.repositories.getLocationOrError
import com.soyle.stories.prose.MentionTextReplaced
import com.soyle.stories.prose.repositories.ProseRepository

class RenameLocationUseCase(
  private val locationRepository: LocationRepository,
  private val proseRepository: ProseRepository
) : RenameLocation {

	override suspend fun invoke(id: Location.Id, name: SingleNonBlankLine, output: RenameLocation.OutputPort) {
		val location = locationRepository.getLocationOrError(id)
		val responseModel = updateIfNamesAreDifferent(name, location) ?: return
		output.receiveRenameLocationResponse(responseModel)
	}

	private suspend fun updateIfNamesAreDifferent(name: SingleNonBlankLine, location: Location): RenameLocation.ResponseModel? {
		return if (name != location.name) {
			RenameLocation.ResponseModel(
				updateLocationName(location, name),
				updateProseThatMentionLocation(location, name)
			)
		}
		else null
	}

	private suspend fun updateLocationName(location: Location, name: SingleNonBlankLine): LocationRenamed {
		locationRepository.updateLocation(location.withName(name))
		return LocationRenamed(location.id, name.value)
	}

	private suspend fun updateProseThatMentionLocation(location: Location, newName: SingleNonBlankLine): List<MentionTextReplaced>
	{
		val locationEntityId = location.id.mentioned()
		val updatedProse = proseRepository.getProseThatMentionEntity(locationEntityId).map {
			it.withMentionTextReplaced(locationEntityId, newName.value)
		}
		proseRepository.replaceProse(updatedProse.map { it.prose })
		return updatedProse.mapNotNull { it.event }
	}
}