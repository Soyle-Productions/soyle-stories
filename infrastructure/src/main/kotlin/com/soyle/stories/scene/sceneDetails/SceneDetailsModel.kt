package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.common.Model
import com.soyle.stories.common.bindImmutableList
import com.soyle.stories.di.get
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneState
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneView
import com.soyle.stories.soylestories.ApplicationScope

class SceneDetailsModel : Model<SceneDetailsScope, SceneDetailsViewModel>(SceneDetailsScope::class) {

	val invalid = bind(SceneDetailsViewModel::invalid)
	val storyEventId = bind(SceneDetailsViewModel::storyEventId)

	val locationSectionLabel = bind(SceneDetailsViewModel::locationSectionLabel)
	val locationDropDownEmptyLabel = bind(SceneDetailsViewModel::locationDropDownEmptyLabel)
	val availableLocations = bind(SceneDetailsViewModel::availableLocations)
	val selectedLocation = bind(SceneDetailsViewModel::selectedLocation)

	val includedCharactersInScene = bind(SceneDetailsViewModel::includedCharactersInScene)

	override fun viewModel(): SceneDetailsViewModel? {
		return item?.copy(
			includedCharactersInScene = scope.get<IncludedCharactersInSceneState>().viewModel()
		)
	}

	override val applicationScope: ApplicationScope
		get() = scope.projectScope.applicationScope

}