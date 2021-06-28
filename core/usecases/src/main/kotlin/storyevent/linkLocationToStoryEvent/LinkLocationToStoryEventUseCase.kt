package com.soyle.stories.usecase.storyevent.linkLocationToStoryEvent

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventRepository
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