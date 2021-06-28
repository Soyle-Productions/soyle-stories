package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.characterList.CharacterItemViewModel
import com.soyle.stories.theme.characterConflict.AvailablePerspectiveCharacterViewModel

data class MoralArgumentViewModel(
    val moralProblemLabel: String,
    val moralProblemValue: String,
    val themeLineLabel: String,
    val themeLineValue: String,
    val thematicRevelationLabel: String,
    val thematicRevelationValue: String,
    val perspectiveCharacterLabel: String,
    val noPerspectiveCharacterLabel: String,
    val selectedPerspectiveCharacter: CharacterItemViewModel?,
    val availablePerspectiveCharacters: List<AvailablePerspectiveCharacterViewModel>?,
    val loadingPerspectiveCharactersLabel: String,
    val loadingSectionTypesLabel: String,
    val createCharacterLabel: String,
    val unavailableCharacterMessage: (AvailablePerspectiveCharacterViewModel) -> String,
    val unavailableSectionTypeMessage: (MoralArgumentSectionTypeViewModel) -> String,
    val removeSectionButtonLabel: String,
    val sections: List<MoralArgumentSectionViewModel>?,
    val availableSectionTypes: List<MoralArgumentSectionTypeViewModel>?
)

data class MoralArgumentSectionViewModel(
    val arcSectionId: String,
    val arcSectionName: String,
    val arcSectionValue: String,
    val canBeRemoved: Boolean
)

data class MoralArgumentSectionTypeViewModel(
    val sectionTypeId: String,
    val sectionTypeName: String,
    val canBeCreated: Boolean,
    val existingArcSectionId: String?
)