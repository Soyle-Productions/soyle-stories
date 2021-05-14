package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.ThemeDriver
import com.soyle.stories.desktop.config.drivers.theme.givenCharacterConflictToolHasBeenOpenedFor
import com.soyle.stories.desktop.config.drivers.theme.givenThemeListToolHasBeenOpened
import com.soyle.stories.desktop.config.drivers.theme.promoteCharacter
import com.soyle.stories.desktop.config.features.soyleStories
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
    }

}