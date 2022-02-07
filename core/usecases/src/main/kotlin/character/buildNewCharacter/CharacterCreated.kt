package com.soyle.stories.usecase.character.buildNewCharacter

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import java.util.*

class CharacterCreated(
    val projectId: Project.Id,
    val characterId: Character.Id,
    val name: String
)