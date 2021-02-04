package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebAssert.Companion.assertThat
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebDriver
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class OppositionValueSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {

    }

    private fun whens() {
        When(
            "an opposition value is created in the {string} value web in the {string} theme"
        ) { valueWebName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
                .run {
                    givenValueWebHasBeenSelectedNamed(valueWebName)
                    createNewOppositionValue()
                }
        }
        When(
            "the first opposition value in the {string} value web in the {string} theme is renamed to {string}"
        ) { valueWebName: String, themeName: String, newOppositionValueName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
                .run {
                    givenValueWebHasBeenSelectedNamed(valueWebName)
                    renameOppositionValueTo(0, newOppositionValueName)
                }
        }
        When(
            "the first opposition value in the {string} value web in the {string} theme is deleted"
        ) { valueWebName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
                .run {
                    givenValueWebHasBeenSelectedNamed(valueWebName)
                    deleteOppositionValue(0)
                }
        }
    }

    private fun thens() {
        Then(
            "an opposition value named {string} should have been created in the {string} value web in the {string} theme"
        ) { oppositionValueName: String, valueWebName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val valueWeb = themeDriver.getValueWebInThemeNamedOrError(theme.id, valueWebName)

            assertEquals(oppositionValueName, valueWeb.oppositions.component2().name) { "Second opposition value in value web $valueWebName was not created and named properly" }

            val valueWebTool = workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
            valueWebTool.givenValueWebHasBeenSelectedNamed(valueWebName)
            assertThat(valueWebTool) {
                andValueWebContent {
                    hasOppositionValueNamed(oppositionValueName)
                }
            }
        }
        Then(
            "the first opposition value in the {string} value web in the {string} theme should have been renamed to {string}"
        ) { valueWebName: String, themeName: String, expectedOppositionValueName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val valueWeb = themeDriver.getValueWebInThemeNamedOrError(theme.id, valueWebName)

            assertEquals(expectedOppositionValueName, valueWeb.oppositions.first().name) { "First opposition value in value web $valueWebName was not renamed" }

            val valueWebTool = workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
            valueWebTool.givenValueWebHasBeenSelectedNamed(valueWebName)
            assertThat(valueWebTool) {
                andValueWebContent {
                    hasOppositionValueNamed(expectedOppositionValueName)
                    doesNotHaveOppositionValueNamed(valueWeb.name)
                }
            }
        }
        Then(
            "the {string} value web in the {string} theme should have no opposition values"
        ) { valueWebName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val valueWeb = themeDriver.getValueWebInThemeNamedOrError(theme.id, valueWebName)

            assertTrue(valueWeb.oppositions.isEmpty()) { "Value ewb $valueWebName should not have opposition values" }

            val valueWebTool = workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
            valueWebTool.givenValueWebHasBeenSelectedNamed(valueWebName)
            assertThat(valueWebTool) {
                andValueWebContent {
                    hasNoOppositionValues()
                }
            }
        }
    }

}