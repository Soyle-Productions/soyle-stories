package com.soyle.stories.scene.sceneList

import com.soyle.stories.scene.items.SceneItemViewModel

data class SceneListViewModel(
  val toolTitle: String,
  val emptyLabel: String,
  val createSceneButtonLabel: String,
  val scenes: List<SceneItemViewModel>,
  val renameSceneFailureMessage: String?
) {
	val hasScenes: Boolean
		get() = scenes.isNotEmpty()
}