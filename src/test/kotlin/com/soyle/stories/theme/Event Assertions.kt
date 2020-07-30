package com.soyle.stories.theme

import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun includedCharacterInTheme(baseCharacter: Character, theme: Theme, asMajorCharacter: Boolean = false) = fun (event: CharacterIncludedInTheme)
{
    assertEquals(baseCharacter.id.uuid, event.characterId)
    assertEquals(baseCharacter.name, event.characterName)
    assertEquals(asMajorCharacter, event.isMajorCharacter)
    assertEquals(theme.id.uuid, event.themeId)
    assertEquals(theme.name, event.themeName)
}

fun opponent(expectedId: UUID, expectedName: String, expectedPerspectiveCharacterId: UUID, expectedThemeId: UUID, expectedToBeMain: Boolean) = fun(actual: Any?) {
    actual as OpponentCharacter
    assertEquals(expectedId, actual.characterId)
    assertEquals(expectedName, actual.characterName)
    assertEquals(expectedPerspectiveCharacterId, actual.opponentOfCharacterId)
    assertEquals(expectedThemeId, actual.themeId)
    assertEquals(expectedToBeMain, actual.isMainOpponent)
}