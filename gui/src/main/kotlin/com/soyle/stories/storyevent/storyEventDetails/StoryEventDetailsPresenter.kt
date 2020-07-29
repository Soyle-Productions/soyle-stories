package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationList.LocationListListener
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
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
) : GetStoryEventDetails.OutputPort, LocationListListener, CharacterListListener {

	private val subPresenters: List<*>

	init {
		val formattedStoryEventId = UUID.fromString(storyEventId)

		subPresenters = listOf(
		  LinkLocationToStoryEventPresenter(formattedStoryEventId, view) listensTo linkLocationToStoryEventNotifier,
		  AddCharacterToStoryEventPresenter(formattedStoryEventId, view) listensTo addCharacterToStoryEventNotifier
		)
	}

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

	override fun receiveLocationListUpdate(locations: List<LocationItem>) {
		view.update {

			val locationViewModels = locations.map(::LocationItemViewModel)

			if (this != null) copy(
			  selectedLocation = selectedLocationId?.let {
				  locationViewModels.find { it.id == selectedLocationId }
			  },
			  locations = locationViewModels
			)
			else {
				StoryEventDetailsViewModel(
				  title = "Story Event Details - [TODO]",
				  locationSelectionButtonLabel = "Select Location",
				  selectedLocationId = null,
				  selectedLocation = null,
				  includedCharacterIds = emptySet(),
				  includedCharacters = emptyList(),
				  locations = locationViewModels,
				  availableCharacters = emptyList(),
				  characters = emptyList()
				)
			}
		}
	}

	override fun receiveCharacterListUpdate(characters: List<CharacterItem>) {
		view.update {

			val viewModels = characters.map { CharacterItemViewModel(it.characterId.toString(), it.characterName, "") }

			if (this != null) copy(
			  availableCharacters = viewModels,
			  includedCharacters = viewModels.filter {
				  it.characterId in includedCharacterIds
			  },
			  characters = viewModels
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
				  availableCharacters = viewModels,
				  characters = viewModels
				)
			}
		}
	}

	override fun receiveGetStoryEventDetailsFailure(failure: StoryEventException) {

	}

}