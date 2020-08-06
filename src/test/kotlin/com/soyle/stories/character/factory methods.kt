package com.soyle.stories.character

import com.soyle.stories.entities.*
import java.util.*

fun makeCharacter(
    id: Character.Id = Character.Id(),
    projectId: Project.Id = Project.Id(),
    name: String = "Character ${UUID.randomUUID().toString().take(3)}",
    media: Media.Id? = null
) = Character(id, projectId, name, media)