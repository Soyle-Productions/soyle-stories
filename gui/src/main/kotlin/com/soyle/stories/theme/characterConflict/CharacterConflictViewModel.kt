package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel

data class CharacterConflictViewModel(
    val centralConflictFieldLabel: String,
    val centralConflict: String,
    val perspectiveCharacterLabel: String,
    val selectedPerspectiveCharacter: CharacterItemViewModel?,
    val availablePerspectiveCharacters: List<CharacterItemViewModel>?,
    val desireLabel: String,
    val desire: String,
    val psychologicalWeaknessLabel: String,
    val psychologicalWeakness: String,
    val moralWeaknessLabel: String,
    val moralWeakness: String,
    val characterChangeLabel: String,
    val characterChange: String
)