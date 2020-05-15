package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import tornadofx.toProperty

class StoryEventDetailsModel : Model<StoryEventDetailsScope, StoryEventDetailsViewModel>(StoryEventDetailsScope::class) {

	override val applicationScope: ApplicationScope
		get() = scope.projectScope.applicationScope

	val title = bind(StoryEventDetailsViewModel::title)
	val locationSelectionButtonLabel = bind(StoryEventDetailsViewModel::locationSelectionButtonLabel)
	val locations = bindImmutableList(StoryEventDetailsViewModel::locations)
	val hasLocations = bind { (! item?.locations.isNullOrEmpty()).toProperty() }

}