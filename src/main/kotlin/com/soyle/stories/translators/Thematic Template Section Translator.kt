package com.soyle.stories.translators

import com.soyle.stories.entities.CharacterArcTemplateSection
import com.soyle.stories.entities.theme.ThematicTemplateSection

fun CharacterArcTemplateSection.asThematicTemplateSection() =
    ThematicTemplateSection(id, name, isRequired, allowsMultiple, isMoral)

fun ThematicTemplateSection.asCharacterArcTemplateSection() =
    CharacterArcTemplateSection(characterArcTemplateSectionId, name, isRequired, allowsMultiple, isMoral)