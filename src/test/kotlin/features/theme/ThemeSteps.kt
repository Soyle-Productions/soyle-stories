package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.character.CharacterDriver
import com.soyle.stories.desktop.config.drivers.project.ProjectDriver
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.soylestories.getWorkbenchForProjectOrError
import com.soyle.stories.desktop.config.drivers.soylestories.getWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.themeList.ThemeListAssert.Companion.assertThat
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
        Given("a theme named {string} has been created") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            ThemeDriver(workbench).givenThemeNamed(themeName)
        }
        Given("the following characters have been included as major characters in the {string} theme") {
                themeName: String, data: DataTable ->

            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val characterDriver = CharacterDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            data.asList().forEach { characterName ->
                val character = characterDriver.getCharacterByNameOrError(characterName)
                themeDriver.givenCharacterIsMajorCharacterInTheme(character.id, theme.id)
            }
        }
    }

    private fun whens() {
        When("a theme is created with the name {string}") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.openCreateThemeDialog()
                .createThemeWithName(themeName)
        }
        When("the {string} theme is renamed with the name {string}") { originalThemeName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .renameThemeTo(originalThemeName, newName)
        }
        When("the {string} theme is deleted") { themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeList = workbench.givenThemeListToolHasBeenOpened()
            themeList.openDeleteThemeDialogForThemeNamed(themeName)
                ?.confirmDeleteTheme()
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
        Then("the theme originally named {string} should have been renamed to {string}") {
                originalThemeName: String, newName: String ->
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
    }

}