package com.soyle.stories.theme.characterValueComparison

import com.soyle.stories.characterarc.characterComparison.CharacterItemViewModel

data class CharacterValueComparisonViewModel(
    val openValueWebToolButtonLabel: String,
    val addCharacterButtonLabel: String,
    val characters: List<CharacterComparedWithValuesViewModel>,
    val availableCharacters: List<CharacterItemViewModel>?,
    val availableOppositionValues: List<AvailableValueWebViewModel>?
)

data class CharacterComparedWithValuesViewModel(
    val characterId: String,
    val characterName: String,
    val archetype: ArchetypeLabel,
    val valueSectionHeaderLabel: String,
    val removeButtonLabel: String,
    val removeButtonToolTip: String,
    val addValueButtonLabel: String,
    val values: List<CharacterValueViewModel>
)

class ArchetypeLabel(
    label: String
) {

    val label: String = label.takeIf { it.isNotBlank() } ?: "(archetype)"
    val isEmptyValue: Boolean = label.isEmpty()
}

class CharacterValueViewModel(val oppositionId: String, val label: String)

class AvailableValueWebViewModel(val valueWebId: String, val label: String, val preSelectedOppositionValue: AvailableOppositionValue?, val availableOppositions: List<AvailableOppositionValue>)
class AvailableOppositionValue(val oppositionId: String, val label: String)