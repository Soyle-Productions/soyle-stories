package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.location.items.LocationItemViewModel

class SceneDetailsViewModel(
  val invalid: Boolean,
  val storyEventId: String?,
  val locationSectionLabel: String,
  val locationDropDownEmptyLabel: String,
  val selectedLocation: LocationItemViewModel?,
  val availableLocations: List<LocationItemViewModel>,
  val charactersSectionLabel: String,
  val addCharacterButtonLabel: String,
  val includedCharacters: List<SceneDetailsCharacterViewModel>,
  val availableCharacters: List<CharacterItemViewModel>,
  val removeCharacterButtonLabel: String,
  val lastChangedTipLabel: String,
  val resentButtonLabel: String,
  internal val characters: List<CharacterItemViewModel>,
  internal val locations: List<LocationItemViewModel>
)

class SceneDetailsCharacterViewModel(
  val characterId: String,
  val characterName: String,
  val motivation: String,
  val previousMotivationSource: SceneDetailsPreviousSceneViewModel?,
  val canReset: Boolean
)

class SceneDetailsPreviousSceneViewModel(
  val sceneId: String,
  val sceneName: String,
  val previousValue: String
)