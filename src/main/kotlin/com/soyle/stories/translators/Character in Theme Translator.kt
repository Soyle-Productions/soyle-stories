package com.soyle.stories.translators

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.theme.characterInTheme.MinorCharacter
import com.soyle.stories.entities.theme.ThematicSection

fun Character.asMinorCharacter(): MinorCharacter {
    return MinorCharacter(
        id,
        name.value,
        "",
        "",
        ""
    )
}