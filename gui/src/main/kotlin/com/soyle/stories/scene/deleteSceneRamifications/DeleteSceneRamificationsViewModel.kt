package com.soyle.stories.scene.deleteSceneRamifications

data class DeleteSceneRamificationsViewModel(
  val invalid: Boolean = true,
  val okMessage: String,
  val scenes: List<SceneRamificationsViewModel>
)

data class SceneRamificationsViewModel(
  val sceneName: String,
  val sceneId: String,
  val characters: List<CharacterRamificationsViewModel>
)

class CharacterRamificationsViewModel(
  val characterName: String,
  val characterId: String,
  val currentMotivation: String,
  val changedMotivation: String
)