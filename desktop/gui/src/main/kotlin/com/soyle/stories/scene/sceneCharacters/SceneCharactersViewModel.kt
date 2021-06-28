package com.soyle.stories.scene.sceneCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

data class SceneCharactersViewModel(
    val targetSceneId: Scene.Id?,
    val availableCharacters: List<AvailableCharacterToAddToSceneViewModel>?,
    val includedCharacters: List<IncludedCharacterViewModel>?
)
data class AvailableCharacterToAddToSceneViewModel(
    val id: Character.Id,
    val name: String,
    val imageSource: String?
)