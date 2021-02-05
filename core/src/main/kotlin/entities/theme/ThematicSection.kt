package com.soyle.stories.entities.theme

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArcSection
import com.soyle.stories.entities.Theme

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 4:04 PM
 */
class ThematicSection(
    val characterArcSectionId: CharacterArcSection.Id,
    val characterId: Character.Id,
    val themeId: Theme.Id,
    val template: ThematicTemplateSection
)