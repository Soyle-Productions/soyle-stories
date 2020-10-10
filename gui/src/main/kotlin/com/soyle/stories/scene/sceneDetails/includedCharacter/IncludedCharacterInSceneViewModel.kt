package com.soyle.stories.scene.sceneDetails.includedCharacter

data class IncludedCharacterInSceneViewModel(
    val characterId: String,
    val characterName: String,
    val motivation: String,
    val motivationCanBeReset: Boolean,
    val previousMotivation: PreviousMotivation?,
    val coveredArcSections: List<CoveredArcSectionViewModel>,
    val availableCharacterArcSections: List<AvailableCharacterArcViewModel>?
)

data class PreviousMotivation(
    val value: String,
    val sourceSceneId: String
)

data class CoveredArcSectionViewModel(
    val arcSectionId: String,
    val characterArcId: String,
    val displayLabel: String
)

class AvailableCharacterArcViewModel(
    val characterArcId: String,
    val characterArcName: String,
    val numberOfCoveredSections: Int,
    val allSectionsCovered: Boolean,
    val sections: List<AvailableArcSectionViewModel>
)

class AvailableArcSectionViewModel(
    val arcSectionId: String,
    val arcSectionLabel: String,
    val isCovered: Boolean,
    val labelWhenSelected: String
)