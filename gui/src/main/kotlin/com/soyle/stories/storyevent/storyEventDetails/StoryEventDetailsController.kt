package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventController

class StoryEventDetailsController(
  private val storyEventId: String,
  private val threadTransformer: ThreadTransformer,
  private val listAllLocations: ListAllLocations,
  private val listAllLocationsOutputPort: ListAllLocations.OutputPort,
  private val linkLocationToStoryEventController: LinkLocationToStoryEventController
) : StoryEventDetailsViewListener {

	override fun getValidState() {
		threadTransformer.async {
			listAllLocations.invoke(listAllLocationsOutputPort)
		}
	}

	override fun deselectLocation() {
		linkLocationToStoryEventController.unlinkLocationToStoryEvent(storyEventId)
	}

	override fun selectLocation(locationId: String) {
		linkLocationToStoryEventController.linkLocationToStoryEvent(storyEventId, locationId)
	}

}