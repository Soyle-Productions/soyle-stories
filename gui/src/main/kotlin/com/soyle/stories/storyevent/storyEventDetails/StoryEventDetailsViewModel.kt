package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.location.items.LocationItemViewModel

data class StoryEventDetailsViewModel(
  val title: String,
  val locationSelectionButtonLabel: String,
  val locations: List<LocationItemViewModel>
)