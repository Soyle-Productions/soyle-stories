package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsMainOpponent
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.CharacterUsedAsOpponent
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun includedCharacterInTheme(baseCharacter: Character, theme: Theme, asMajorCharacter: Boolean = false) = fun (event: CharacterIncludedInTheme)
{
    assertEquals(baseCharacter.id.uuid, event.characterId)
    assertEquals(baseCharacter.displayName.value, event.characterName)
    assertEquals(asMajorCharacter, event.isMajorCharacter)
    assertEquals(theme.id.uuid, event.themeId)
    assertEquals(theme.name, event.themeName)
}

fun opponent(expectedId: UUID, expectedName: String, expectedPerspectiveCharacterId: UUID, expectedThemeId: UUID, expectedToBeMain: Boolean) = fun(actual: Any?) {
    if (expectedToBeMain) {
        actual as CharacterUsedAsMainOpponent
        assertEquals(expectedId, actual.characterId)
        assertEquals(expectedName, actual.characterName)
        assertEquals(expectedPerspectiveCharacterId, actual.opponentOfCharacterId)
        assertEquals(expectedThemeId, actual.themeId)
    } else {
        actual as CharacterUsedAsOpponent
        assertEquals(expectedId, actual.characterId)
        assertEquals(expectedName, actual.characterName)
        assertEquals(expectedPerspectiveCharacterId, actual.opponentOfCharacterId)
        assertEquals(expectedThemeId, actual.themeId)
    }
}