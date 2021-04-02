package com.soyle.stories.usecase.character.arc.section.removeCharacterArcSectionFromMoralArgument

import java.util.*

data class CharacterArcSectionRemoved(
    val arcSectionId: UUID,
    val themeId: UUID,
    val characterId: UUID,
    val arcId: UUID
)