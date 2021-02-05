package com.soyle.stories.characterarc.usecases.removeCharacterArcSectionFromMoralArgument

import java.util.*

data class CharacterArcSectionRemoved(
    val arcSectionId: UUID,
    val themeId: UUID,
    val characterId: UUID,
    val arcId: UUID
)