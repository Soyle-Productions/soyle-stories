package com.soyle.stories.theme.characterConflict

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel
import com.soyle.stories.theme.usecases.examineCentralConflictOfTheme.CharacterChangeOpponent

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
    val characterChange: String,
    val opponentSectionsLabel: String,
    val attackSectionLabel: String,
    val similaritiesSectionLabel: String,
    val powerStatusOrAbilitiesLabel: String,
    val opponents: List<CharacterChangeOpponentViewModel>,
    val availableOpponents: List<AvailableOpponentViewModel>?
)

class CharacterChangeOpponentViewModel(
    val characterId: String,
    val characterName: String,
    val attack: String,
    val similarities: String,
    val powerStatusOrAbilities: String
)

class AvailableOpponentViewModel(
    val characterId: String,
    val characterName: String,
    val isInTheme: Boolean
)