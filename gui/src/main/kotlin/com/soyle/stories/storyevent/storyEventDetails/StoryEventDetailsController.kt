package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventController

class StoryEventDetailsController(
  private val storyEventId: String,
  private val threadTransformer: ThreadTransformer,
  private val listAllLocations: ListAllLocations,
  private val listAllLocationsOutputPort: ListAllLocations.OutputPort,
  private val listAllCharacters: ListAllCharacterArcs,
  private val listAllCharactersOutputPort: ListAllCharacterArcs.OutputPort,
  private val linkLocationToStoryEventController: LinkLocationToStoryEventController
) : StoryEventDetailsViewListener {

	override fun getValidState() {
		threadTransformer.async {
			listAllLocations.invoke(listAllLocationsOutputPort)
		}
		threadTransformer.async {
			listAllCharacters.invoke(listAllCharactersOutputPort)
		}
	}

	override fun deselectLocation() {
		linkLocationToStoryEventController.unlinkLocationToStoryEvent(storyEventId)
	}

	override fun selectLocation(locationId: String) {
		linkLocationToStoryEventController.linkLocationToStoryEvent(storyEventId, locationId)
	}

}