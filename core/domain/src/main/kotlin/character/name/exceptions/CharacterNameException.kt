package com.soyle.stories.domain.character.name.exceptions

import com.soyle.stories.domain.character.exceptions.CharacterException

interface CharacterNameException : CharacterException {
    val name: String
}