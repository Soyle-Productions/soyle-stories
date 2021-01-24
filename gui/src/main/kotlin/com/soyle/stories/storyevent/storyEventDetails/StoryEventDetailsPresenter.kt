package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.character.usecases.removeCharacterFromStory.RemovedCharacter
import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.entities.CharacterRenamed
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationList.LocationListListener
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.addCharacterToStoryEvent.IncludedCharacterInStoryEventReceiver
import com.soyle.stories.storyevent.storyEventDetails.presenters.AddCharacterToStoryEventPresenter
import com.soyle.stories.storyevent.storyEventDetails.presenters.LinkLocationToStoryEventPresenter
import com.soyle.stories.storyevent.usecases.getStoryEventDetails.GetStoryEventDetails
import com.soyle.stories.storyevent.usecases.linkLocationToStoryEvent.LinkLocationToStoryEvent
import java.util.*

class StoryEventDetailsPresenter(
  storyEventId: String,
  private val view: View.Nullable<StoryEventDetailsViewModel>,
  linkLocationToStoryEventNotifier: Notifier<LinkLocationToStoryEvent.OutputPort>,
  includedCharacterInStoryEventNotifier: Notifier<IncludedCharacterInStoryEventReceiver>
) : GetStoryEventDetails.OutputPort, LocationListListener, CharacterListListener {

	private val subPresenters: List<*>

	init {
		val formattedStoryEventId = UUID.fromString(storyEventId)

		subPresenters = listOf(
		  LinkLocationToStoryEventPresenter(formattedStoryEventId, view) listensTo linkLocationToStoryEventNotifier,
		  AddCharacterToStoryEventPresenter(formattedStoryEventId, view) listensTo includedCharacterInStoryEventNotifier
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

	override suspend fun receiveCreatedCharacter(createdCharacter: CreatedCharacter) {
		val newCharacter = CharacterItemViewModel(
			createdCharacter.characterId.toString(),
			createdCharacter.characterName,
			createdCharacter.mediaId?.toString() ?: ""
		)
		view.updateOrInvalidated {
			copy(
				availableCharacters = availableCharacters + newCharacter,
				characters = characters + newCharacter
			)
		}
	}

	override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
		val renamedCharacterId= characterRenamed.characterId.toString()
		view.updateOrInvalidated {
			copy(
				availableCharacters = availableCharacters.map {
					if (it.characterId == renamedCharacterId) it.copy(characterName = characterRenamed.newName)
					else it
				},
				characters = characters.map {
					if (it.characterId == renamedCharacterId) it.copy(characterName = characterRenamed.newName)
					else it
				}
			)
		}
	}

	override suspend fun receiveCharacterRemoved(characterRemoved: RemovedCharacter) {
		val removedCharacterId= characterRemoved.characterId.toString()
		view.updateOrInvalidated {
			copy(
				availableCharacters = availableCharacters.filterNot { it.characterId == removedCharacterId },
				includedCharacters = includedCharacters.filterNot { it.characterId == removedCharacterId },
				characters = characters.filterNot { it.characterId == removedCharacterId }
			)
		}
	}

	override fun receiveGetStoryEventDetailsFailure(failure: StoryEventException) {

	}

}