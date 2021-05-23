package com.soyle.stories.usecase.location.renameLocation

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.LocationRenamed
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.prose.mentioned
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.prose.ProseRepository
import com.soyle.stories.usecase.scene.SceneRepository

class RenameLocationUseCase(
  private val locationRepository: LocationRepository,
  private val proseRepository: ProseRepository,
  private val sceneRepository: SceneRepository
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
				updateProseThatMentionLocation(location, name),
				updateScenesThatUseLocation(location, name)
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
			it.withMentionTextReplaced(locationEntityId, location.name.value to newName.value)
		}
		proseRepository.replaceProse(updatedProse.map { it.prose })
		return updatedProse.mapNotNull { it.event }
	}

	private suspend fun updateScenesThatUseLocation(location: Location, newName: SingleNonBlankLine): List<SceneSettingLocationRenamed>
	{
		val renamedLocation = location.withName(newName)
		val sceneUpdates = sceneRepository.getScenesUsingLocation(location.id).mapNotNull {
			it.withLocationRenamed(renamedLocation) as? Updated
		}
		sceneRepository.updateScenes(sceneUpdates.map { it.scene })
		return sceneUpdates.map { it.event }
	}
}