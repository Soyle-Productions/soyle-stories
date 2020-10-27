package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel

data class MoralArgumentViewModel(
    val moralProblemLabel: String,
    val moralProblemValue: String,
    val themeLineLabel: String,
    val themeLineValue: String,
    val perspectiveCharacterLabel: String,
    val noPerspectiveCharacterLabel: String,
    val selectedPerspectiveCharacter: CharacterItemViewModel?,
    val availablePerspectiveCharacters: List<AvailablePerspectiveCharacterViewModel>?,
    val loadingPerspectiveCharactersLabel: String,
    val createCharacterLabel: String,
    val unavailableCharacterMessage: (AvailablePerspectiveCharacterViewModel) -> String,
    val sections: List<MoralArgumentSectionViewModel>?
)

data class MoralArgumentSectionViewModel(
    val arcSectionId: String,
    val arcSectionName: String,
    val arcSectionValue: String
)