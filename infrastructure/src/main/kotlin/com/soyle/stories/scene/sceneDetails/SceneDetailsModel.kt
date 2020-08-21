package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.soylestories.ApplicationScope

class SceneDetailsModel : Model<SceneDetailsScope, SceneDetailsViewModel>(SceneDetailsScope::class) {

	val invalid = bind(SceneDetailsViewModel::invalid)
	val storyEventId = bind(SceneDetailsViewModel::storyEventId)
	val locationSectionLabel = bind(SceneDetailsViewModel::locationSectionLabel)
	val locationDropDownEmptyLabel = bind(SceneDetailsViewModel::locationDropDownEmptyLabel)
	val charactersSectionLabel = bind(SceneDetailsViewModel::charactersSectionLabel)
	val addCharacterButtonLabel = bind(SceneDetailsViewModel::addCharacterButtonLabel)
	val removeCharacterButtonLabel = bind(SceneDetailsViewModel::removeCharacterButtonLabel)
	val lastChangedTipLabel = bind(SceneDetailsViewModel::lastChangedTipLabel)
	val resentButtonLabel = bind(SceneDetailsViewModel::resentButtonLabel)
	val availableCharacters = bind(SceneDetailsViewModel::availableCharacters)
	val availableLocations = bind(SceneDetailsViewModel::availableLocations)

	val includedCharacters = bind(SceneDetailsViewModel::includedCharacters)
	val selectedLocation = bind(SceneDetailsViewModel::selectedLocation)

	override val applicationScope: ApplicationScope
		get() = scope.projectScope.applicationScope

}