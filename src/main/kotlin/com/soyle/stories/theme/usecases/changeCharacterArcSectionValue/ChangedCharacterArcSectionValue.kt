package com.soyle.stories.theme.usecases.changeCharacterArcSectionValue

import java.util.*

class ChangedCharacterArcSectionValue(
    val arcSectionId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val type: ArcSectionType,
    val newValue: String
)