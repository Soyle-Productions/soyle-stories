package com.soyle.stories.domain.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcSection

class ThematicSection(
    val characterArcSectionId: CharacterArcSection.Id,
    val characterId: Character.Id,
    val themeId: Theme.Id,
    val template: ThematicTemplateSection
)