package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.characterConflict.`Character Conflict Assertions`.Companion.assertThat
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import io.cucumber.java8.En

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
            "I change the {character}'s psychological weakness in the {theme} to {string}"
        ) { character: Character, theme: Theme, weakness: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(character.id)
                .changePsychologicalWeaknessTo(weakness)
        }
        When(
            "I change the {character}'s moral weakness in the {theme} to {string}"
        ) { character: Character, theme: Theme, weakness: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(character.id)
                .changeMoralWeaknessTo(weakness)
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
            "the {character}'s psychological weakness in the {theme} should be {string}"
        ) { character: Character, theme: Theme, expectedWeakness: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(character.id)
                .assertThat {
                    psychologicalWeaknessHasValue(expectedWeakness)
                }
        }
        Then(
            "the {character}'s moral weakness in the {theme} should be {string}"
        ) { character: Character, theme: Theme, expectedWeakness: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenThemeListToolHasBeenOpened()
                .givenCharacterConflictToolHasBeenOpenedFor(theme.id)
                .givenFocusedOnPerspectiveCharacter(character.id)
                .assertThat {
                    moralWeaknessHasValue(expectedWeakness)
                }
        }
    }

}