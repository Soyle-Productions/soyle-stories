package com.soyle.stories.domain.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project

data class CharacterAddedToStory(override val characterId: Character.Id, val projectId: Project.Id, val primaryName: String) : CharacterEvent()