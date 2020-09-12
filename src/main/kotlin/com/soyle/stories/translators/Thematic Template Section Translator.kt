package com.soyle.stories.translators

import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.theme.ThematicTemplateSection

/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 12:03 PM
 */
fun CharacterArcTemplateSection.asThematicTemplateSection() =
    ThematicTemplateSection(id, name, isRequired)

fun ThematicTemplateSection.asCharacterArcTemplateSection() =
    CharacterArcTemplateSection(characterArcTemplateSectionId, name, isRequired)