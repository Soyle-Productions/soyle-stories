package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.character.CharacterArcDriver
import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.moralArgument.MoralArgumentViewAssert.Companion.assertThat
import com.soyle.stories.desktop.view.theme.themeList.ThemeListAssert.Companion.assertThat
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArcTemplateSection
import com.soyle.stories.domain.theme.Theme
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class ThemeSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I have created a theme named {string}") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            ThemeDriver(workbench).givenThemeNamed(themeName)
        }
        Given("the following characters have been included as major characters in the {string} theme") { themeName: String, data: DataTable ->

            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val characterDriver = CharacterDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            data.asList().forEach { characterName ->
                val character = characterDriver.getCharacterByNameOrError(characterName)
                themeDriver.givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
            }
        }
        Given("{character} has been included in {theme} as a major character") { character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            themeDriver.givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
        }
        Given(
            "the character {string} has been included in the {string} theme as a major character"
        ) { characterName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val characterDriver = CharacterDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val character = characterDriver.getCharacterByNameOrError(characterName)
            themeDriver.givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
        }
        Given(
            "{template} has been added to {character}'s {theme} moral argument"
        ) { template: CharacterArcTemplateSection, character: Character, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val arcDriver = CharacterArcDriver(workbench)
            val arc = arcDriver.getCharacterArcForCharacterAndThemeOrError(character.id, theme.id)
            arcDriver.givenArcHasArcSectionInMoralArgument(arc, template)
        }
        Given("I have deleted the {string} theme") { themeName: String ->
            ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError()).givenThemeDeletedNamed(themeName)
        }
    }

    private fun whens() {
        When("I create a theme named {string}") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.openCreateThemeDialog()
                .createThemeWithName(themeName)
        }
        When("I rename the {theme} to {string}") { theme: Theme, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .renameThemeTo(theme, newName)
        }
        When("I delete the {theme}") { theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .openDeleteThemeDialogForThemeNamed(theme.name)
                ?.confirmDeleteTheme()
        }
        When("the {string} theme is deleted") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeList = workbench.givenThemeListToolHasBeenOpened()
            themeList.openDeleteThemeDialogForThemeNamed(themeName)
                ?.confirmDeleteTheme()
        }
        When(
            "the moral problem for the {string} theme is changed to {string}"
        ) { themeName: String, moralProblem: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.changeMoralProblemTo(moralProblem)
        }
        When(
            "the theme line for the {string} theme is changed to {string}"
        ) { themeName: String, themeLine: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.changeThemeLineTo(themeLine)
        }
        When(
            "the thematic revelation for the {string} theme is changed to {string}"
        ) { themeName: String, thematicRevelation: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            moralArgument.changeThematicRevelationTo(thematicRevelation)

        }
    }

    private fun thens() {
        Then("a theme named {string} should have been created") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            ThemeDriver(workbench).getThemeByNameOrError(themeName)

            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                hasThemeNamed(themeName)
            }
        }
        Then("the theme originally named {string} should have been renamed to {string}") { originalThemeName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeAtOnePointNamedOrError(originalThemeName).second!!

            assertEquals(newName, theme.name) { "Theme $originalThemeName was not renamed to $newName" }

            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                doesNotHaveThemeNamed(originalThemeName)
                hasThemeNamed(newName)
            }

        }
        Then("the {string} theme should have been deleted") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            assertNull(ThemeDriver(workbench).getThemeByName(themeName)) { "Theme $themeName not deleted" }

            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                doesNotHaveThemeNamed(themeName)
            }

        }
        Then(
            "the moral problem for the {string} theme should be {string}"
        ) { themeName: String, expectedMoralProblem: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            assertEquals(
                expectedMoralProblem,
                theme.centralMoralProblem
            ) { "$themeName's moral problem was not updated." }

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andMoralProblemField {
                    hasValue(expectedMoralProblem)
                }
            }
        }
        Then(
            "the theme line for the {string} theme should be {string}"
        ) { themeName: String, expectedThemeLine: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            assertEquals(expectedThemeLine, theme.themeLine) { "$themeName's theme line was not updated." }

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andThemeLineField {
                    hasValue(expectedThemeLine)
                }
            }
        }
        Then(
            "the thematic revelation for the {string} theme should be {string}"
        ) { themeName: String, expectedRevelation: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val theme = ThemeDriver(workbench).getThemeByNameOrError(themeName)
            assertEquals(expectedRevelation, theme.thematicRevelation) { "$themeName's thematic revelation was not updated." }

            val moralArgument = workbench.givenMoralArgumentToolHasBeenOpenedForTheme(theme)
            assertThat(moralArgument) {
                andThematicRevelationField {
                    hasValue(expectedRevelation)
                }
            }
        }
    }

}