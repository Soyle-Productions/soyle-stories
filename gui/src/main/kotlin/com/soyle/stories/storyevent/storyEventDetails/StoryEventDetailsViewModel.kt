package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.location.items.LocationItemViewModel

data class StoryEventDetailsViewModel(
  val title: String,
  val locationSelectionButtonLabel: String,
  val selectedLocation: LocationItemViewModel?,
  val includedCharacters: List<CharacterItemViewModel>,
  val locations: List<LocationItemViewModel>,
  val characters: List<CharacterItemViewModel>
)