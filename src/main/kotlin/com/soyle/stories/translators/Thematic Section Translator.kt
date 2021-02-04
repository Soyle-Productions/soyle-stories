package com.soyle.stories.translators

import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.theme.ThematicSection

fun CharacterArcSection.asThematicSection() = ThematicSection(
    id,
    characterId,
    themeId,
    template.asThematicTemplateSection()
)

fun ThematicSection.asCharacterArcSection() = CharacterArcSection(
    characterArcSectionId,
    characterId,
    themeId,
    template.asCharacterArcTemplateSection(),
    null,
    ""
)