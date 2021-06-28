package com.soyle.stories.character.profile

import com.soyle.stories.domain.character.Character

interface CharacterProfileProps {
    val characterId: Character.Id
    val imageResource: String
    val name: String
}