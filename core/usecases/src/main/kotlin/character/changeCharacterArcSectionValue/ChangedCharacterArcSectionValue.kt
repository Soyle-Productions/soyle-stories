package com.soyle.stories.usecase.character.changeCharacterArcSectionValue

import java.util.*

class ChangedCharacterArcSectionValue(
    val arcSectionId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val type: ArcSectionType?,
    val newValue: String
)