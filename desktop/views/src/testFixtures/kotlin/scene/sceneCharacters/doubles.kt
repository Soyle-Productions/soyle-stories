package com.soyle.stories.desktop.view.scene.sceneCharacters

import com.soyle.stories.domain.character.Character
import com.soyle.stories.scene.sceneCharacters.*

fun makeIncludedCharacterViewModel(
    id: Character.Id = Character.Id(),
    name: String = "Character Name",
    imageResource: String = "",
    roleInScene: CharacterRoleInScene? = null,
    desire: String = "",
    motivation: String = "",
    motivationCanBeReset: Boolean = false,
    previousMotivation: PreviousMotivation? = null,
    coveredArcSections: List<CoveredArcSectionViewModel> = listOf(),
    availableCharacterArcSections: List<AvailableCharacterArcViewModel>? = null
) = IncludedCharacterViewModel(
    id,
    name,
    imageResource,
    roleInScene,
    desire,
    motivation,
    motivationCanBeReset,
    previousMotivation,
    coveredArcSections,
    availableCharacterArcSections
)