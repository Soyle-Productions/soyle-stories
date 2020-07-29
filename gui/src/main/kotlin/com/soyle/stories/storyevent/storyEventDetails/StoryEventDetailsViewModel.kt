package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.location.items.LocationItemViewModel

data class StoryEventDetailsViewModel(
  val title: String,
  val locationSelectionButtonLabel: String,
  internal val selectedLocationId: String?,
  val selectedLocation: LocationItemViewModel?,
  internal val includedCharacterIds: Set<String>,
  val includedCharacters: List<CharacterItemViewModel>,
  val locations: List<LocationItemViewModel>,
  val availableCharacters: List<CharacterItemViewModel>,
  internal val characters: List<CharacterItemViewModel>
)