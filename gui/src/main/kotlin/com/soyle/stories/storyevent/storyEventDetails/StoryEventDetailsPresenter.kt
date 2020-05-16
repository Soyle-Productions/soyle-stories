package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.storyevent.storyEventDetails.presenters.AddCharacterToStoryEventPresenter
import com.soyle.stories.storyevent.storyEventDetails.presenters.LinkLocationToStoryEventPresenter
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent

class StoryEventDetailsPresenter(
  private val view: View.Nullable<StoryEventDetailsViewModel>,
  linkLocationToStoryEventNotifier: Notifier<LinkLocationToStoryEvent.OutputPort>,
  addCharacterToStoryEventNotifier: Notifier<AddCharacterToStoryEvent.OutputPort>
) : ListAllLocations.OutputPort, ListAllCharacterArcs.OutputPort {

	private var selectedLocationId: String? = null

	private val subPresenters = listOf(
	  LinkLocationToStoryEventPresenter(view) listensTo linkLocationToStoryEventNotifier,
	  AddCharacterToStoryEventPresenter(view) listensTo addCharacterToStoryEventNotifier
	)

	override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
		view.update {

			val locations = response.locations.map(::LocationItemViewModel)

			if (this != null) copy(locations = locations)
			else {
				StoryEventDetailsViewModel(
				  title = "Story Event Details - [TODO]",
				  locationSelectionButtonLabel = "Select Location",
				  selectedLocation = selectedLocationId?.let { id -> locations.find { it.id == id } },
				  includedCharacters = emptyList(),
				  locations = locations,
				  characters = emptyList()
				)
			}
		}
	}

	override fun receiveCharacterArcList(response: ListAllCharacterArcs.ResponseModel) {
		view.update {

			val characters = response.characters.map { CharacterItemViewModel(it.key.characterId.toString(), it.key.characterName) }

			if (this != null) copy(characters = characters)
			else {
				StoryEventDetailsViewModel(
				  title = "Story Event Details - [TODO]",
				  locationSelectionButtonLabel = "Select Location",
				  selectedLocation = null,
				  includedCharacters = emptyList(),
				  locations = emptyList(),
				  characters = characters
				)
			}

		}
	}

}