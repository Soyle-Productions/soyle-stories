package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.common.isListeningTo
import com.soyle.stories.location.locationList.LiveLocationList
import com.soyle.stories.location.locationList.LocationListListener
import com.soyle.stories.storyevent.addCharacterToStoryEvent.AddCharacterToStoryEventController
import com.soyle.stories.storyevent.linkLocationToStoryEvent.LinkLocationToStoryEventController
import com.soyle.stories.storyevent.removeCharacterFromStoryEvent.RemoveCharacterFromStoryEventController
import com.soyle.stories.storyevent.usecases.getStoryEventDetails.GetStoryEventDetails
import java.util.*

class StoryEventDetailsController(
  private val storyEventId: String,
  private val threadTransformer: ThreadTransformer,
  private val getStoryEventDetails: GetStoryEventDetails,
  private val getStoryEventDetailsOutputPort: GetStoryEventDetails.OutputPort,
  private val liveCharacterList: LiveCharacterList,
  private val characterListListener: CharacterListListener,
  private val liveLocationList: LiveLocationList,
  private val locationListListener: LocationListListener,
  private val linkLocationToStoryEventController: LinkLocationToStoryEventController,
  private val addCharacterToStoryEventController: AddCharacterToStoryEventController,
  private val removeCharacterFromStoryEventController: RemoveCharacterFromStoryEventController
) : StoryEventDetailsViewListener {

	init {
		liveCharacterList.addListener(characterListListener)
	}

	override fun getValidState() {
		threadTransformer.async {
			getStoryEventDetails.invoke(UUID.fromString(storyEventId), getStoryEventDetailsOutputPort)
		}
		if (locationListListener isListeningTo liveLocationList) {
			liveLocationList.removeListener(locationListListener)
		}
		liveLocationList.addListener(locationListListener)
		// TODO get character list
	}

	override fun deselectLocation() {
		linkLocationToStoryEventController.unlinkLocationToStoryEvent(storyEventId)
	}

	override fun selectLocation(locationId: String) {
		linkLocationToStoryEventController.linkLocationToStoryEvent(storyEventId, locationId)
	}

	override fun addCharacter(characterId: String) {
		addCharacterToStoryEventController.addCharacterToStoryEvent(storyEventId, characterId)
	}

	override fun removeCharacter(characterId: String) {
		removeCharacterFromStoryEventController.removeCharacter(storyEventId, characterId)
	}

}