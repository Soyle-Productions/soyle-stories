package com.soyle.stories.theme.usecases.changeCharacterDesire

import java.util.*

class ChangedCharacterDesire(
    val arcSectionId: UUID,
    val characterId: UUID,
    val themeId: UUID,
    val newValue: String
)