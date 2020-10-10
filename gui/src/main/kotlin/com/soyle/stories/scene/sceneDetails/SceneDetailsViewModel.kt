package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneViewModel

data class SceneDetailsViewModel(
  val invalid: Boolean,
  val storyEventId: String?,
  val locationSectionLabel: String,
  val locationDropDownEmptyLabel: String,
  val selectedLocation: LocationItemViewModel?,
  val availableLocations: List<LocationItemViewModel>,

  val includedCharactersInScene: IncludedCharactersInSceneViewModel?,

  internal val characters: List<CharacterItemViewModel>,
  internal val locations: List<LocationItemViewModel>
)
