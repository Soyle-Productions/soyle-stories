package com.soyle.stories.domain.character.name.events

import com.soyle.stories.domain.character.Character

data class CharacterDisplayNameSelected(
    override val characterId: Character.Id,
    val oldName: String,
    override val name: String
) : CharacterNameChange()