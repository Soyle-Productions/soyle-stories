package com.soyle.stories.usecase.character.arc.section.changeCharacterArcSectionValue

import java.util.*

data class ChangedCharacterArcSectionValue(
    val arcSectionId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val type: ArcSectionType?,
    val newValue: String
)