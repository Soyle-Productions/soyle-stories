package com.soyle.stories.storyevent.storyEventList

import com.soyle.stories.storyevent.items.StoryEventListItemViewModel

data class StoryEventListViewModel(
  val toolTitle: String,
  val emptyLabel: String,
  val createStoryEventButtonLabel: String,
  val storyEvents: List<StoryEventListItemViewModel>,
  val renameStoryEventFailureMessage: String?
)