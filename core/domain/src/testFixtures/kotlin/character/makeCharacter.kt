package com.soyle.stories.domain.character

import com.soyle.stories.domain.media.Media
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.str
import com.soyle.stories.domain.validation.NonBlankString

fun characterName() = nonBlankStr("Character ${str()}")

fun makeCharacter(
    id: Character.Id = Character.Id(),
    projectId: Project.Id = Project.Id(),
    name: NonBlankString = characterName(),
    media: Media.Id? = null
) = Character(id, projectId, name, setOf(), media)