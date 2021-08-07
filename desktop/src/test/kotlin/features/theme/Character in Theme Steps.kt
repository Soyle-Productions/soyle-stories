package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.character.createCharacterWithName
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.characterComparison.`Character Comparison Assertions`.Companion.assertThat
import com.soyle.stories.desktop.view.theme.characterComparison.`Character Comparison Assertions`.`Included Character Assertions`.Companion.includedCharacter
import com.soyle.stories.desktop.view.theme.characterConflict.`Character Conflict Assertions`.Companion.assertThat
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.fail

class `Character in Theme Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I have included the {character} in the {theme}") { character: Character, theme: Theme ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterIsIncludedInTheme(character.id, theme.id)
        }
        Given(
            "I have promoted the {character} to a major character in the {theme}"
        ) { character: Character, theme: Theme ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
        }
        Given(
            "I am examining the {theme}'s central conflict for the {character}"
        ) { theme: Theme, character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(character.id)
        }
        Given(
            "I have added the {character} as an opponent to {string} in the {theme}"
        ) { toBeOpponent: Character, perspectiveCharacterName: String, theme: Theme ->
            val perspectiveCharacterId = theme.characters.single { it.name == perspectiveCharacterName }.id
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(perspectiveCharacterId)
                .addOpponentCharacter(toBeOpponent)
        }
        Given(
            "I am comparing the character values of the {theme}"
        ) { theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
        }
        Given(
            "I have included the {character} in the {theme}'s character value comparison"
        ) { character: Character, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .givenCharacterHasBeenAdded(character)
        }
        Given(
            "I am selecting a value to add to the {character} in the {theme}'s character value comparison"
        ) { character: Character, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .givenCharacterHasBeenAdded(character)
                .givenAvailableValuesHaveBeenLoadedFor(character.id)
        }
        Given(
            "I have used the {theme}'s {string} value web's {string} opposition value for the {character}"
        ) { theme: Theme, valueWebName: String, oppositionName: String, character: Character ->
            val valueWeb = theme.valueWebs.single { it.name.value == valueWebName }
            val oppositionValue = valueWeb.oppositions.single { it.name.value == oppositionName }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .givenOppositionValueUsedForCharacter(character.id, valueWeb, oppositionValue)
        }
    }

    private fun whens() {
        When(
            "I promote the {character} to a major character in the {theme}"
        ) { character: Character, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .promoteCharacter(character.id)
        }
        When(
            "I demote the {character} to a minor character in the {theme}"
        ) { character: Character, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
        }
        When(
            "I change the {character}'s {} in the {theme} to {string}"
        ) { character: Character, arcSection: String, theme: Theme, newValue: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(character.id)
                .changeFieldValueTo(arcSection, newValue)

        }
        When(
            "I examine the {theme}'s central conflict for the {character}"
        ) { theme: Theme, character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .focusOnPerspectiveCharacter(character.id)
        }
        When(
            "I include the {character} in the {theme}'s character value comparison"
        ) { character: Character, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .addCharacter(character)
        }
        When(
            "I create a character named {string} to include in the {theme}'s character value comparison"
        ) { characterName: String, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .givenCreateCharacterDialogHasBeenOpened()
                .createCharacterWithName(characterName)
        }
        When(
            "I select the {theme}'s {string} value web's {string} opposition value to add to the {character}"
        ) { theme: Theme, valueWebName: String, oppositionName: String, character: Character ->
            val valueWeb = theme.valueWebs.single { it.name.value == valueWebName }
            val oppositionValue = valueWeb.oppositions.single { it.name.value == oppositionName }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .givenAvailableValuesHaveBeenLoadedFor(character.id)
                .selectOppositionValue(valueWeb, oppositionValue)
        }
        When(
            "I create an opposition value named {string} in the {theme}'s {string} value web to add to the {character}"
        ) { newOppositionName: String, theme: Theme, valueWebName: String, character: Character ->
            val valueWeb = theme.valueWebs.single { it.name.value == valueWebName }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .givenAvailableValuesHaveBeenLoadedFor(character.id)
                .givenCreateOppositionValueDialogHasBeenOpenedFor(valueWeb.id)
                .createOppositionValueNamed(newOppositionName)
        }
        When(
            "I create a value web named {string} in the {theme} to add to the {character}"
        ) { newValueWebName: String, theme: Theme, character: Character ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .givenAvailableValuesHaveBeenLoadedFor(character.id)
                .givenCreateValueWebDialogHasBeenOpened()
                .createValueWebNamed(newValueWebName)
        }
    }

    private fun thens() {
        Then(
            "the {character} should be a minor character in the {theme}"
        ) { character: Character, theme: Theme ->
            theme.getMinorCharacterById(character.id) ?: error("Character is not a minor character")
        }
        Then(
            "the {character} should be a major character in the {theme}"
        ) { character: Character, theme: Theme ->
            theme.getMajorCharacterByIdOrError(character.id)
        }
        Then(
            "the {character} should be included in the {theme}'s character value comparison"
        ) { character: Character, theme: Theme ->
            theme.getIncludedCharacterById(character.id)
                ?: fail("Character ${character.name} was not included in the ${theme.name} theme")

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .assertThat {
                    hasIncludedCharacter(character.id)
                }

        }
        Then(
            "the {character}'s {} in the {theme} should be {string}"
        ) { character: Character, arcSectionName: String, theme: Theme, expectedValue: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(character.id)
                .assertThat {
                    access.characterChangeInput(arcSectionName)!!.hasValue(expectedValue)
                }
        }
        Then(
            "the {character} should have no values in the {theme}"
        ) { character: Character, theme: Theme ->
            theme.valueWebs.flatMap { it.oppositions }.forEach {
                assertFalse(it.hasEntityAsRepresentation(character.id.uuid))
            }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .assertThat {
                    includedCharacter(character.id)!!.hasNoValues()
                }
        }
        Then(
            "the {character} in the {theme} should have the {string} value web's {string} opposition value"
        ) { character: Character, theme: Theme, valueWebName: String, oppositionName: String ->
            val valueWeb = theme.valueWebs.single { it.name.value == valueWebName }
            val oppositionValue = valueWeb.oppositions.single { it.name.value == oppositionName }
            assertTrue(oppositionValue.hasEntityAsRepresentation(character.id.uuid))

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .assertThat {
                    includedCharacter(character.id)!!.hasValue(valueWeb, oppositionValue)
                }
        }
        Then(
            "the {character} in the {theme} should not have the {string} value web's {string} opposition value"
        ) { character: Character, theme: Theme, valueWebName: String, oppositionName: String ->
            val valueWeb = theme.valueWebs.single { it.name.value == valueWebName }
            val oppositionValue = valueWeb.oppositions.singleOrNull() { it.name.value == oppositionName }
            if (oppositionValue != null) {
                assertFalse(oppositionValue.hasEntityAsRepresentation(character.id.uuid))
            }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterComparisonToolHasBeenOpenedFor(theme.id)
                .assertThat {
                    includedCharacter(character.id)!!.doesNotHaveValue(valueWebName, oppositionName)
                }
        }
    }

}