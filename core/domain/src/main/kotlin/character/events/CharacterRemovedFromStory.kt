package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project

data class CharacterRemovedFromStory(
    val characterId: Character.Id,
    val projectId: Project.Id
)