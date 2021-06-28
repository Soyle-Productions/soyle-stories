package com.soyle.stories.domain.character

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme
import java.util.*

fun makeCharacterArcSection(
    id: CharacterArcSection.Id = CharacterArcSection.Id(UUID.randomUUID()),
    characterId: Character.Id = Character.Id(),
    themeId: Theme.Id = Theme.Id(),
    template: CharacterArcTemplateSection = Desire,
    linkedLocation: Location.Id? = null,
    value: String = "Character Arc Section Value ${template.name} ${str()}"
) = CharacterArcSection(
    id, characterId, themeId, template, linkedLocation, value
)