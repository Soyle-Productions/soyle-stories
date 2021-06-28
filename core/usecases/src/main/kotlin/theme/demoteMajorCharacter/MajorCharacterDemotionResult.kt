package com.soyle.stories.usecase.theme.demoteMajorCharacter

import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter

internal class MajorCharacterDemotionResult(
    val updatedTheme: Theme,
    val demotedCharacter: MajorCharacter,
    val removedArcSections: List<CharacterArcSection.Id>

)