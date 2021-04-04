package com.soyle.stories.domain.character

import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme

fun makeCharacterArc(
    characterId: Character.Id = Character.Id(),
    themeId: Theme.Id = Theme.Id(),
    name: String = "Character Arc ${str()}",
    template: CharacterArcTemplate = CharacterArcTemplate.default()
) = CharacterArc.planNewCharacterArc(
    characterId, themeId, name, template
)