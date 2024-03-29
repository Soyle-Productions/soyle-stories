package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebAssert.Companion.assertThat
import com.soyle.stories.desktop.view.theme.oppositionWebTool.ValueOppositionWebAssert.Companion.assertThis
import com.soyle.stories.domain.theme.Theme
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.*

class ValueWebSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given(
            "I have created a value web named {string} in the {theme}"
        ) { valueWebName: String, theme: Theme ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenValueWebToolHasBeenOpenedFor(theme.id)
                .givenValueWebHasBeenCreatedNamed(valueWebName)
        }
        Given(
            "the {string} symbol in the {string} theme has been added as a symbolic item to the opposition value in the {string} value web"
        ) { symbolName: String, themeName: String, valueWebName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val symbol = themeDriver.getSymbolInThemeNamedOrError(theme.id, symbolName)
            val valueWeb = themeDriver.getValueWebInThemeNamedOrError(theme.id, valueWebName)

            themeDriver.givenSymbolicItemInThemeAddedToOpposition(symbol.id, valueWeb.oppositions.first().id, symbol.id.uuid::equals)
        }
    }

    private fun whens() {
        When("a value web is created with the name {string} in the {string} theme") { valueWebName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
                .openCreateValueWebDialog()
                .createValueWebNamed(valueWebName)
        }
        When(
            "I rename the {theme}'s {string} value web to {string}"
        ) { theme: Theme, valueWebName: String, newName: String ->
            val valueWeb = theme.valueWebs.single { it.name.value == valueWebName }
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenValueWebToolHasBeenOpenedFor(theme.id)
                .renameValueWebTo(valueWeb.name.value, newName)
        }
        When(
            "the {string} value web in the {string} theme is renamed to {string}"
        ) { valueWebName: String, themeName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
                .renameValueWebTo(valueWebName, newName)
        }
        When("the {string} value web in the {string} theme is deleted") { valueWebName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenValueWebToolHasBeenOpenedForThemeNamed(themeName)
                .openDeleteValueWebDialogForValueWebNamed(valueWebName)
                ?.confirmDeleteValueWeb()
        }
    }

    private fun thens() {
        Then(
            "the {theme} should have a value web named {string}"
        ) { theme: Theme, expectedValueWebName: String ->
            val valueWeb = theme.valueWebs.single { it.name.value == expectedValueWebName }

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenValueWebToolHasBeenOpenedFor(theme.id)
                .assertThis {
                    hasValueWebNamed(valueWeb.name.value)
                }
        }
        Then(
            "the {theme} should not have a value web named {string}"
        ) { theme: Theme, unexpectedValueWebName: String ->
            assertTrue(theme.valueWebs.none { it.name.value == unexpectedValueWebName })

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenValueWebToolHasBeenOpenedFor(theme.id)
                .assertThis {
                    doesNotHaveValueWebNamed(unexpectedValueWebName)
                }
        }
        Then(
            "all symbolic items for the symbol originally named {string} in the {string} theme should have been renamed to {string}"
        ) { originalSymbolicItemName: String, themeName: String, expectedName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val symbolId = themeDriver.getSymbolInThemeAtOnePointNamedOrError(theme.id, originalSymbolicItemName).first

            val symbolicItems = themeDriver.getSymbolicItemsForItem { it == symbolId.uuid }
            symbolicItems.forEach {
                assertEquals(expectedName, it.name)
            }
        }
        Then(
            "all symbolic items for the {string} symbol in the {string} theme should have been removed from all opposition values"
        ) { symbolName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val symbolId = themeDriver.getSymbolInThemeAtOnePointNamedOrError(theme.id, symbolName).first

            val symbolicItems = themeDriver.getSymbolicItemsForItem { it == symbolId.uuid }
            assertTrue(symbolicItems.isEmpty()) { "No symbolic items should remain for symbol $symbolName" }
        }
    }

}