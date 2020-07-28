package com.soyle.stories.theme

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import org.junit.jupiter.api.Assertions.assertEquals

fun includedCharacterInTheme(baseCharacter: Character, theme: Theme, asMajorCharacter: Boolean = false) = fun (event: CharacterIncludedInTheme)
{
    assertEquals(baseCharacter.id.uuid, event.characterId)
    assertEquals(baseCharacter.name, event.characterName)
    assertEquals(asMajorCharacter, event.isMajorCharacter)
    assertEquals(theme.id.uuid, event.themeId)
    assertEquals(theme.name, event.themeName)
}