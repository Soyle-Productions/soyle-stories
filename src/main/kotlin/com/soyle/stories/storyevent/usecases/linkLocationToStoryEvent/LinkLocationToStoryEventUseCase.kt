package com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent

import com.soyle.stories.entities.Location
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.storyevent.StoryEventDoesNotExist
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import java.util.*

class LinkLocationToStoryEventUseCase(
  private val storyEventRepository: StoryEventRepository,
  private val locationRepository: LocationRepository
) : LinkLocationToStoryEvent {
	override suspend fun invoke(storyEventId: UUID, locationId: UUID?, outputPort: LinkLocationToStoryEvent.OutputPort) {
		val response = try {
			linkLocationToStoryEvent(storyEventId, locationId)
		} catch (e: Exception) {
			return outputPort.receiveLinkLocationToStoryEventFailure(e)
		}
		outputPort.receiveLinkLocationToStoryEventResponse(response)
	}

	private suspend fun linkLocationToStoryEvent(storyEventId: UUID, locationId: UUID?): LinkLocationToStoryEvent.ResponseModel {
		val storyEvent = getStoryEvent(storyEventId)
		val newLocation = locationId?.let { getLocation(it) }

		storyEventRepository.updateStoryEvent(storyEvent.withLocationId(newLocation?.id))
		return LinkLocationToStoryEvent.ResponseModel(storyEventId, locationId, storyEvent.linkedLocationId?.uuid)
	}

	private suspend fun getLocation(locationId: UUID) =
	  (locationRepository.getLocationById(Location.Id(locationId))
		?: throw LocationDoesNotExist(locationId))

	private suspend fun getStoryEvent(storyEventId: UUID) =
	  (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId))
		?: throw StoryEventDoesNotExist(storyEventId))
}