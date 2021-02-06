package com.soyle.stories.domain.theme

import com.soyle.stories.domain.character.CharacterArcTemplateSection

data class ThematicTemplateSection(
    val characterArcTemplateSectionId: CharacterArcTemplateSection.Id,
    val name: String,
    val isRequired: Boolean,
    val allowsMultiple: Boolean,
    val isMoral: Boolean
)