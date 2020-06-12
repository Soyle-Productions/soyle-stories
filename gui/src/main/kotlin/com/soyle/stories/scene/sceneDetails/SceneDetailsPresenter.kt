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
import com.soyle.stories.scene.usecases.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.scene.usecases.removeCharacterFromScene.RemoveCharacterFromScene
import com.soyle.stories.scene.usecases.reorderScene.ReorderScene
import com.soyle.stories.scene.usecases.setMotivationForCharacterInScene.SetMotivationForCharacterInScene
import java.util.*

class SceneDetailsPresenter(
  sceneId: String,
  private val view: View.Nullable<SceneDetailsViewModel>,
  characterList: LiveCharacterList,
  locationList: LiveLocationList,
  characterIncludedInScene: Notifier<IncludeCharacterInScene.OutputPort>,
  locationLinkedToScene: Notifier<LinkLocationToScene.OutputPort>,
  characterRemovedFromScene: Notifier<RemoveCharacterFromScene.OutputPort>,
  characterMotivationSet: Notifier<SetMotivationForCharacterInScene.OutputPort>,
  sceneReordered: Notifier<ReorderScene.OutputPort>
) : GetSceneDetails.OutputPort,
  CharacterListListener,
  LocationListListener,
  IncludeCharacterInScene.OutputPort,
  LinkLocationToScene.OutputPort,
  RemoveCharacterFromScene.OutputPort,
  SetMotivationForCharacterInScene.OutputPort,
	ReorderScene.OutputPort {

	private val sceneId = UUID.fromString(sceneId)

	init {
		this listensTo characterList
		this listensTo locationList
		this listensTo characterIncludedInScene
		this listensTo locationLinkedToScene
		this listensTo characterRemovedFromScene
		this listensTo characterMotivationSet
		this listensTo sceneReordered
	}

	override fun sceneDetailsRetrieved(response: GetSceneDetails.ResponseModel) {
		view.update {
			val locationId = response.locationId?.toString()
			val includedCharacterIds = response.characters.map { it.characterId.toString() }.toSet()
			copyOrDefault(
			  invalid = false,
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
			  },
			  selectedLocation = locationId?.let {
				  (this?.locations ?: listOf()).find { it.id == locationId }
					?: LocationItemViewModel(locationId, "")
			  },
			  availableLocations = (this?.locations ?: listOf()).filter {
				  it.id != locationId
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
			val characterNames = characterViewModels.associate { it.characterId to it.characterName }
			copyOrDefault(
			  characters = characterViewModels,
			  includedCharacters = (this?.includedCharacters ?: listOf()).mapNotNull {
				  if (it.characterId !in characterNames) return@mapNotNull null
				  SceneDetailsCharacterViewModel(
					it.characterId,
					characterNames.getValue(it.characterId),
					it.motivation,
					it.previousMotivationSource,
					it.canReset
				  )
			  },
			  availableCharacters = characterViewModels.filter {
				  it.characterId !in includedCharacterIds
			  }
			)
		}
	}

	override fun receiveLocationListUpdate(locations: List<LocationItem>) {
		view.update {
			val locationId = this?.selectedLocation?.id
			val locationViewModels = locations.map {
				LocationItemViewModel(it.id.toString(), it.locationName)
			}
			copyOrDefault(
			  selectedLocation = locationId?.let {
				  locationViewModels.find { it.id == locationId }
			  },
			  availableLocations = locationViewModels.filter {
				  it.id != locationId
			  },
			  locations = locationViewModels
			)
		}
	}

	override fun characterIncludedInScene(response: IncludeCharacterInScene.ResponseModel) {
		if (response.sceneId != sceneId) return
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

	override fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
		if (response.sceneId != sceneId) return
		view.updateOrInvalidated {
			val locationId = response.locationId?.toString()
			copyOrDefault(
			  selectedLocation = locationId?.let {
				  locations.find { it.id == locationId }
			  },
			  availableLocations = locations.filter {
				  it.id != locationId
			  }
			)
		}
	}

	override fun characterRemovedFromScene(response: RemoveCharacterFromScene.ResponseModel) {
		if (response.sceneId != sceneId) return
		val characterId = response.characterId.toString()
		view.updateOrInvalidated {
			val remainingCharacters = includedCharacters.filterNot { it.characterId == characterId }
			val remainingCharacterIds = remainingCharacters.map { it.characterId }.toSet()
			copyOrDefault(
			  includedCharacters = includedCharacters.filterNot { it.characterId == characterId },
			  availableCharacters = characters.filterNot {
				  it.characterId in remainingCharacterIds
			  }
			)
		}
	}

	override fun motivationSetForCharacterInScene(response: SetMotivationForCharacterInScene.ResponseModel) {
		if (response.sceneId != sceneId) return
		val characterId = response.characterId.toString()
		view.updateOrInvalidated {
			if (response.motivation == null) {
				return@updateOrInvalidated copyOrDefault(invalid = true)
			}

			copyOrDefault(
			  includedCharacters = includedCharacters.map {
				  if (it.characterId != characterId) it
				  else {
					  SceneDetailsCharacterViewModel(
						it.characterId,
						it.characterName,
						response.motivation ?: "",
						it.previousMotivationSource,
						true
					  )
				  }
			  }
			)
		}
	}

	override fun sceneReordered(response: ReorderScene.ResponseModel) {
		view.updateOrInvalidated {
			copyOrDefault(invalid = true)
		}
	}

	private fun SceneDetailsViewModel?.copyOrDefault(
	  invalid: Boolean = this?.invalid ?: true,
	  storyEventId: String? = this?.storyEventId,
	  locationSectionLabel: String = this?.locationSectionLabel ?: "Setting",
	  locationDropDownEmptyLabel: String = this?.locationDropDownEmptyLabel ?: "Select Location",
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
	  invalid,
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
	override fun failedToLinkLocationToScene(failure: Exception) {
		println(failure)
	}

	override fun failedToRemoveCharacterFromScene(failure: Exception) {
		println(failure)
	}

	override fun failedToSetMotivationForCharacterInScene(failure: Exception) {
		println(failure)
	}

	override fun failedToReorderScene(failure: Exception) {}

}