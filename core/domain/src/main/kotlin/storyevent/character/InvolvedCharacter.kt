package com.soyle.stories.domain.storyevent.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.entities.Entity

class InvolvedCharacter(
    override val id: Character.Id,
    val name: String
) : Entity<Character.Id> {

    internal fun withName(newName: String) = InvolvedCharacter(id, newName)

}