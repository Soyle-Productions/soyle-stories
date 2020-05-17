package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.storyEventDetails.presenters.AddCharacterToStoryEventPresenter
import com.soyle.stories.storyevent.storyEventDetails.presenters.LinkLocationToStoryEventPresenter
import com.soyle.stories.storyevent.usecases.addCharacterToStoryEvent.AddCharacterToStoryEvent
import com.soyle.stories.storyevent.usecases.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent
import java.util.*

class StoryEventDetailsPresenter(
  storyEventId: String,
  private val view: View.Nullable<StoryEventDetailsViewModel>,
  linkLocationToStoryEventNotifier: Notifier<LinkLocationToStoryEvent.OutputPort>,
  addCharacterToStoryEventNotifier: Notifier<AddCharacterToStoryEvent.OutputPort>
) : GetStoryEventDetails.OutputPort, ListAllLocations.OutputPort, ListAllCharacterArcs.OutputPort {

	private val subPresenters = listOf(
	  LinkLocationToStoryEventPresenter(view) listensTo linkLocationToStoryEventNotifier,
	  AddCharacterToStoryEventPresenter(UUID.fromString(storyEventId), view) listensTo addCharacterToStoryEventNotifier
	)

	override fun receiveGetStoryEventDetailsResponse(response: GetStoryEventDetails.ResponseModel) {
		view.update {

			val includedCharacterIds = response.includedCharacterIds.map(UUID::toString).toSet()

			if (this != null) copy(
			  title = "Story Event Details - ${response.storyEventName}",
			  selectedLocationId = response.locationId.toString(),
			  selectedLocation = response.locationId?.let {
				  val selectedLocationId = it.toString()
				  locations.find { it.id == selectedLocationId }
			  },
			  includedCharacterIds = includedCharacterIds,
			  includedCharacters = characters.filter {
				  it.characterId in includedCharacterIds
			  }
			)
			else StoryEventDetailsViewModel(
			  title = "Story Event Details - ${response.storyEventName}",
			  locationSelectionButtonLabel = "Select Location",
			  selectedLocationId = response.locationId.toString(),
			  selectedLocation = null,
			  includedCharacterIds = includedCharacterIds,
			  includedCharacters = emptyList(),
			  locations = emptyList(),
			  availableCharacters = emptyList(),
			  characters = emptyList()
			)

		}
	}

	override fun receiveListAllLocationsResponse(response: ListAllLocations.ResponseModel) {
		view.update {

			val locations = response.locations.map(::LocationItemViewModel)

			if (this != null) copy(
			  selectedLocation = selectedLocationId?.let {
				  locations.find { it.id == selectedLocationId }
			  },
			  locations = locations
			)
			else {
				StoryEventDetailsViewModel(
				  title = "Story Event Details - [TODO]",
				  locationSelectionButtonLabel = "Select Location",
				  selectedLocationId = null,
				  selectedLocation = null,
				  includedCharacterIds = emptySet(),
				  includedCharacters = emptyList(),
				  locations = locations,
				  availableCharacters = emptyList(),
				  characters = emptyList()
				)
			}
		}
	}

	override fun receiveCharacterArcList(response: ListAllCharacterArcs.ResponseModel) {
		view.update {

			val characters = response.characters.map { CharacterItemViewModel(it.key.characterId.toString(), it.key.characterName) }

			if (this != null) copy(
			  availableCharacters = characters,
			  includedCharacters = characters.filter {
				  it.characterId in includedCharacterIds
			  },
			  characters = characters
			)
			else {
				StoryEventDetailsViewModel(
				  title = "Story Event Details - [TODO]",
				  locationSelectionButtonLabel = "Select Location",
				  selectedLocationId = null,
				  selectedLocation = null,
				  includedCharacterIds = emptySet(),
				  includedCharacters = emptyList(),
				  locations = emptyList(),
				  availableCharacters = characters,
				  characters = characters
				)
			}

		}
	}

	override fun receiveGetStoryEventDetailsFailure(failure: StoryEventException) {

	}

}