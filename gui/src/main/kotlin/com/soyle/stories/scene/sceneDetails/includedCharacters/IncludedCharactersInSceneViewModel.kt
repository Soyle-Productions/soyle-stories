package com.soyle.stories.scene.sceneDetails.includedCharacters

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.scene.sceneDetails.includedCharacter.IncludedCharacterInSceneViewModel

data class IncludedCharactersInSceneViewModel(
    val title: String,
    val storyEventId: String,
    val addCharacterLabel: String,
    val removeCharacterLabel: String,
    val positionOnCharacterArcLabel: String,
    val motivationFieldLabel: String,
    val resetMotivationLabel: String,
    val motivationLastChangedLabel: String,
    val includedCharactersInScene: List<IncludedCharacterInSceneViewModel>,
    val availableCharactersToAdd: List<CharacterItemViewModel>?
)

