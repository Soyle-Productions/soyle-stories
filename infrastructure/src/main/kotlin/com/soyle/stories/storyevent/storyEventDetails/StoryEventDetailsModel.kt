package com.soyle.stories.storyevent.storyEventDetails

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope
import tornadofx.selectBoolean
import tornadofx.toProperty

class StoryEventDetailsModel : Model<StoryEventDetailsScope, StoryEventDetailsViewModel>(StoryEventDetailsScope::class) {

	override val applicationScope: ApplicationScope
		get() = scope.projectScope.applicationScope

	val title = bind(StoryEventDetailsViewModel::title)
	val locationSelectionButtonLabel = bind(StoryEventDetailsViewModel::locationSelectionButtonLabel)
	val selectedLocation = bind(StoryEventDetailsViewModel::selectedLocation)
	val locations = bind(StoryEventDetailsViewModel::locations)
	val includedCharacters = bind(StoryEventDetailsViewModel::includedCharacters)
	val hasLocations = locations.selectBoolean { (! it.isNullOrEmpty()).toProperty() }
	val availableCharacters = bind(StoryEventDetailsViewModel::availableCharacters)
	val hasCharacters = availableCharacters.selectBoolean { (! it.isNullOrEmpty()).toProperty() }

}