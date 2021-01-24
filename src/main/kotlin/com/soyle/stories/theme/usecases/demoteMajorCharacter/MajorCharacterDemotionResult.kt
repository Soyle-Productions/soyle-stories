package com.soyle.stories.theme.usecases.demoteMajorCharacter

import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter

internal class MajorCharacterDemotionResult(
    val updatedTheme: Theme,
    val demotedCharacter: MajorCharacter,
    val removedArcSections: List<CharacterArcSection.Id>

)