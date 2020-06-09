package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.character.characterList.CharacterListListener
import com.soyle.stories.character.characterList.LiveCharacterList
import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.listensTo
import com.soyle.stories.gui.View
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationList.LiveLocationList
import com.soyle.stories.location.locationList.LocationListListener
import com.soyle.stories.location.usecases.listAllLocations.LocationItem
import com.soyle.stories.scene.usecases.getSceneDetails.GetSceneDetails
import com.soyle.stories.scene.usecases.includeCharacterInScene.IncludeCharacterInScene

class SceneDetailsPresenter(
  private val view: View.Nullable<SceneDetailsViewModel>,
  characterList: LiveCharacterList,
  locationList: LiveLocationList,
  characterIncludedInScene: Notifier<IncludeCharacterInScene.OutputPort>
) : GetSceneDetails.OutputPort, CharacterListListener, LocationListListener, IncludeCharacterInScene.OutputPort {

	init {
		this listensTo characterList
		this listensTo locationList
		this listensTo characterIncludedInScene
	}

	override fun sceneDetailsRetrieved(response: GetSceneDetails.ResponseModel) {
		view.update {
			val includedCharacterIds = response.characters.map { it.characterId.toString() }.toSet()
			copyOrDefault(
			  storyEventId = response.storyEventId.toString(),
			  includedCharacters = response.characters.map {
				  SceneDetailsCharacterViewModel(
					it.characterId.toString(),
					it.characterName,
					it.motivation ?: it.inheritedMotivation?.motivation ?: "",
					it.inheritedMotivation?.let {
						SceneDetailsPreviousSceneViewModel(
						  it.sceneId.toString(),
						  it.sceneName,
						  it.motivation
						)
					},
					it.motivation != null
				  )
			  },
			  availableCharacters = (this?.characters ?: listOf()).filter {
				  it.characterId !in includedCharacterIds
			  }
			)
		}
	}

	override fun receiveCharacterListUpdate(characters: List<CharacterItem>) {
		view.update {
			val includedCharacterIds = this?.includedCharacters?.map { it.characterId }?.toSet()
			  ?: setOf()
			val characterViewModels = characters.map {
				CharacterItemViewModel(it.characterId.toString(), it.characterName)
			}
			copyOrDefault(
			  characters = characterViewModels,
			  availableCharacters = characterViewModels.filter {
				  it.characterId !in includedCharacterIds
			  }
			)
		}
	}

	override fun receiveLocationListUpdate(locations: List<LocationItem>) {
		view.update {
			val locationViewModels = locations.map {
				LocationItemViewModel(it.id.toString(), it.locationName)
			}
			copyOrDefault(
			  availableLocations = locationViewModels.filter {
				  it.id != this?.selectedLocation?.id
			  },
			  locations = locationViewModels
			)
		}
	}

	override fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
		view.updateOrInvalidated {
			val includedCharacterIds = this.includedCharacters.map { it.characterId }.toSet() +
			  response.characterDetails.characterId.toString()

			copyOrDefault(
			  includedCharacters = includedCharacters + SceneDetailsCharacterViewModel(
				response.characterDetails.characterId.toString(),
				response.characterDetails.characterName,
				response.characterDetails.motivation ?: "",
				response.characterDetails.inheritedMotivation?.let {
					SceneDetailsPreviousSceneViewModel(
					  it.sceneId.toString(),
					  it.sceneName,
					  it.motivation
					)
				},
				response.characterDetails.motivation != null
			  ),
			  availableCharacters = characters.filter {
				  it.characterId !in includedCharacterIds
			  }
			)
		}
	}

	private fun SceneDetailsViewModel?.copyOrDefault(
	  storyEventId: String? = this?.storyEventId,
	  locationSectionLabel: String = this?.locationSectionLabel ?: "Locations",
	  locationDropDownEmptyLabel: String = this?.locationDropDownEmptyLabel ?: "Location",
	  selectedLocation: LocationItemViewModel? = this?.selectedLocation,
	  availableLocations: List<LocationItemViewModel> = this?.availableLocations ?: listOf(),
	  charactersSectionLabel: String = this?.charactersSectionLabel ?: "Characters",
	  addCharacterButtonLabel: String = this?.addCharacterButtonLabel ?: "Add Character",
	  includedCharacters: List<SceneDetailsCharacterViewModel> = this?.includedCharacters ?: listOf(),
	  availableCharacters: List<CharacterItemViewModel> = this?.availableCharacters ?: listOf(),
	  removeCharacterButtonLabel: String = this?.removeCharacterButtonLabel ?: "Remove Character",
	  lastChangedTipLabel: String = this?.lastChangedTipLabel ?: "When was this last set?",
	  resentButtonLabel: String = this?.resentButtonLabel ?: "Reset to last value.",
	  characters: List<CharacterItemViewModel> = this?.characters ?: listOf(),
	  locations: List<LocationItemViewModel> = this?.locations ?: listOf()
	) = SceneDetailsViewModel(
	  storyEventId,
	  locationSectionLabel,
	  locationDropDownEmptyLabel,
	  selectedLocation,
	  availableLocations,
	  charactersSectionLabel,
	  addCharacterButtonLabel,
	  includedCharacters,
	  availableCharacters,
	  removeCharacterButtonLabel,
	  lastChangedTipLabel,
	  resentButtonLabel,
	  characters,
	  locations
	)

	override fun failedToGetSceneDetails(failure: Exception) {}
	override fun failedToIncludeCharacterInScene(failure: Exception) {}

}