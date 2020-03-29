package com.soyle.stories.translators

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.entities.theme.ThematicSection

/**
 * Created by Brendan
 * Date: 2/26/2020
 * Time: 10:23 PM
 */
fun Character.asMinorCharacter(thematicSections: List<ThematicSection>): MinorCharacter {
    return MinorCharacter(
        id,
        name,
        "",
        "",
        thematicSections
    )
}