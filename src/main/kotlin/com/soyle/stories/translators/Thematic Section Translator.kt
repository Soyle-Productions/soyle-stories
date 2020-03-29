package com.soyle.stories.translators

import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.theme.ThematicSection

/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 4:26 PM
 */
fun CharacterArcSection.asThematicSection() = ThematicSection(
    id,
    characterId,
    themeId,
    template.asThematicTemplateSection()
)