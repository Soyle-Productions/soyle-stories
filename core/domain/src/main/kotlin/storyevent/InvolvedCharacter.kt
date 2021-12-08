package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.entities.Entity

class InvolvedCharacter(
    override val id: Character.Id,
    val name: String
) : Entity<Character.Id>