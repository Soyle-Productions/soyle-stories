package com.soyle.stories.character.usecases

import com.soyle.stories.character.CharacterNameCannotBeBlank

fun validateCharacterName(name: String)
{
	if (name.isBlank()) throw CharacterNameCannotBeBlank
}