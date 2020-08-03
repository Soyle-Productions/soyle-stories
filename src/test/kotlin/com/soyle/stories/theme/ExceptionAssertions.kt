/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 11:00 AM
 */
package com.soyle.stories.theme

import com.soyle.stories.theme.usecases.SymbolNameCannotBeBlank
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*

fun ThemeDoesNotExist.themeIdMustEqual(value: Any) = assert(themeId == value) { "themeId of ThemeDoesNotExist does not equal $value" }

fun themeNameCannotBeBlank(actual: Any?) {
    actual as ThemeNameCannotBeBlank
}

fun themeDoesNotExist(themeId: UUID): (Any?) -> Unit = { actual ->
    actual as ThemeDoesNotExist
    assertEquals(themeId, actual.themeId) { "Unexpected theme id for ThemeDoesNotExist error" }
}

fun characterNotInTheme(themeId: UUID, characterId: UUID) = fun (actual: Any?) {
    actual as CharacterNotInTheme
    assertEquals(themeId, actual.themeId) { "Character not in theme exception does not have expected theme id" }
    assertEquals(characterId, actual.characterId) { "Character not in theme exception does not have expected character id" }
}

fun characterIsNotMajorCharacterInTheme(themeId: UUID, characterId: UUID) = fun (actual: Any?) {
    actual as CharacterIsNotMajorCharacterInTheme
    if (themeId == actual.characterId && characterId == actual.themeId) {
        error("Character not major character in theme exception has characterId and themeId swapped.\n" +
                "Should be themeId = $themeId and characterId = $characterId.\n" +
                "Instead found themeId = $characterId and characterId = $themeId")
    }
    assertEquals(themeId, actual.themeId) { "Character not major character in theme exception does not have expected theme id" }
    assertEquals(characterId, actual.characterId) { "Character not major character in theme exception does not have expected character id" }
}

fun characterIsAlreadyMajorCharacterInTheme(themeId: UUID, characterId: UUID) = fun (actual: Any?) {
    actual as CharacterIsAlreadyMajorCharacterInTheme
    assertEquals(themeId, actual.themeId) { "Character is already major character in theme exception does not have expected theme id" }
    assertEquals(characterId, actual.characterId) { "Character is already major character in theme exception does not have expected character id" }
}

fun symbolNameCannotBeBlank(actual: Any?)
{
    actual as SymbolNameCannotBeBlank
}

fun symbolDoesNotExist(symbolId: UUID): (Any?) -> Unit = { actual ->
    actual as SymbolDoesNotExist
    assertEquals(symbolId, actual.entityId)
    assertEquals(symbolId, actual.symbolId)
}

fun symbolAlreadyHasName(symbolId: UUID, name: String): (Any?) -> Unit = { actual ->
    actual as SymbolAlreadyHasName
    assertEquals(symbolId, actual.symbolId)
    assertEquals(name, actual.symbolName)
}

fun valueWebDoesNotExist(valueWebId: UUID): (Any?) -> Unit = { actual ->
    actual as ValueWebDoesNotExist
    assertEquals(valueWebId, actual.entityId)
    assertEquals(valueWebId, actual.valueWebId)
}

fun oppositionValueDoesNotExist(oppositionValueId: UUID): (Any?) -> Unit = { actual ->
    actual as OppositionValueDoesNotExist
    assertEquals(oppositionValueId, actual.oppositionValueId) { "OppositionValueId does not match expected uuid" }
    assertEquals(oppositionValueId, actual.entityId) { "entityId does not match expected uuid" }
}

fun oppositionValueAlreadyHasName(oppositionValueId: UUID, expectedName: String): (Any?) -> Unit = { actual ->
    actual as OppositionValueAlreadyHasName
    assertEquals(oppositionValueId, actual.oppositionValueId) { "[OppositionValueAlreadyHasName] has incorrect id" }
    assertEquals(expectedName, actual.oppositionValueName) { "[OppositionValueAlreadyHasName] has incorrect name" }
}

fun valueWebDoesNotContainOppositionValue(valueWebId: UUID, oppositionValueId: UUID): (Any?) -> Unit = { actual ->
    actual as ValueWebDoesNotContainOppositionValue
    assertEquals(valueWebId, actual.valueWebId)
    assertEquals(oppositionValueId, actual.oppositionValueId)
    assertEquals(oppositionValueId, actual.entityId)
}

fun valueWebAlreadyHasName(valueWebId: UUID, name: String): (Any?) -> Unit = { actual ->
    actual as ValueWebAlreadyHasName
    assertEquals(valueWebId, actual.valueWebId)
    assertEquals(name, actual.valueWebName)
}

fun symbolicRepresentationNotInOppositionValue(oppositionValueId: UUID, symbolicEntityId: UUID) = fun (actual: Any?) {
    actual as SymbolicRepresentationNotInOppositionValue
    assertEquals(oppositionValueId, actual.oppositionValueId)
    assertEquals(symbolicEntityId, actual.symbolicRepresentationId)
    assertEquals(symbolicEntityId, actual.entityId)
}