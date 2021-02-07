package com.soyle.stories.domain.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.characterInTheme.CharacterInTheme
import com.soyle.stories.domain.theme.characterInTheme.MinorCharacter

fun makeCharacterInTheme(
    characterId: Character.Id = Character.Id(),
    name: String = "character ${str()}",
    archetype: String = "",
    variationOnMoral: String = "",
    position: String = ""
): CharacterInTheme
{
    return MinorCharacter(
        characterId,
        name,
        archetype,
        variationOnMoral,
        position
    )
}