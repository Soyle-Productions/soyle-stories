package com.soyle.stories.entities.theme

import com.soyle.stories.entities.CharacterArcTemplateSection

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 4:07 PM
 */
data class ThematicTemplateSection(
    val characterArcTemplateSectionId: CharacterArcTemplateSection.Id,
    val name: String,
    val isRequired: Boolean,
    val allowsMultiple: Boolean
)