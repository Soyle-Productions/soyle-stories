package com.soyle.stories.character

import com.soyle.stories.common.Desire
import com.soyle.stories.common.str
import com.soyle.stories.entities.*
import java.util.*

fun makeCharacter(
    id: Character.Id = Character.Id(),
    projectId: Project.Id = Project.Id(),
    name: String = "Character ${UUID.randomUUID().toString().take(3)}",
    media: Media.Id? = null
) = Character(id, projectId, name, media)

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