package com.soyle.stories.desktop.config.features.theme

import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.*
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.theme.themeList.ThemeListAssert.Companion.assertThat
import com.soyle.stories.entities.Theme
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

class SymbolSteps : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I have created a symbol named {string} in the {theme}") { symbolName: String, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            themeDriver.givenSymbolInThemeNamed(theme.id, symbolName)
        }
        Given("a symbol named {string} has been created in the {string} theme") { symbolName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            themeDriver.givenSymbolInThemeNamed(theme.id, symbolName)
        }
        Given("I have created the following themes and symbols") { dataTable: DataTable ->
            val dataLists = dataTable.asLists()
            val themeNames = dataLists.first()
            val themeDriver = ThemeDriver(soyleStories.getAnyOpenWorkbenchOrError())
            themeNames.forEachIndexed { index, name ->
                val theme = themeDriver.givenThemeNamed(name)
                dataLists.drop(1).forEach { row ->
                    val symbolName = row.getOrNull(index)?.takeUnless { it.isBlank() } ?: return@forEach
                    themeDriver.givenSymbolInThemeNamed(theme.id, symbolName)
                }
            }
        }
        Given("I have removed the {string} symbol from the {theme}") { symbolName: String, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            themeDriver.givenSymbolRemovedFromTheme(theme, symbolName)
        }
    }

    private fun whens() {
        When("a symbol is created with the name {string} in the {string} theme") { symbolName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .openCreateSymbolDialogForThemeNamed(themeName)
                .createSymbolWithName(symbolName)
        }
        When(
            "the {string} symbol in the {string} theme is renamed with the name {string}"
        ) { originalSymbolName: String, themeName: String, newName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .renameSymbolInThemeTo(originalSymbolName, themeName, newName)
        }
        When("I remove the {string} symbol from the {theme}") { symbolName: String, theme: Theme ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .openDeleteSymbolDialogForSymbolInTheme(theme, symbolName)
                ?.confirmDeleteSymbol()
        }
        When("the {string} symbol in the {string} theme is deleted") { symbolName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenThemeListToolHasBeenOpened()
                .openDeleteSymbolDialogForSymbolInThemeNamed(themeName, symbolName)
                ?.confirmDeleteSymbol()
        }
    }

    private fun thens() {
        Then("a symbol named {string} should have been created in the {string} theme") { symbolName: String, themeName: String ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            themeDriver.getSymbolInThemeNamedOrError(theme.id, symbolName)

            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                hasThemeNamed(themeName)
                andThemeItemNamed(themeName) {
                    hasSymbolNamed(symbolName)
                }
            }
        }
        Then("the symbol originally named {string} in the {string} theme should have been renamed to {string}") {
            originalSymbolName: String, themeName: String, newName: String ->

            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val symbol = themeDriver.getSymbolInThemeAtOnePointNamedOrError(theme.id, originalSymbolName).second!!

            assertEquals(newName, symbol.name)
            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                hasThemeNamed(themeName)
                andThemeItemNamed(themeName) {
                    hasSymbolNamed(newName)
                    doesNotHaveSymbolNamed(originalSymbolName)
                }
            }
        }
        Then("the {string} symbol in the {string} theme should have been deleted") { symbolName: String, themeName: String ->

            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val themeDriver = ThemeDriver(workbench)
            val theme = themeDriver.getThemeByNameOrError(themeName)
            val (_, symbol) = themeDriver.getSymbolInThemeAtOnePointNamedOrError(theme.id, symbolName)

            assertNull(symbol) { "Symbol ${symbol!!.name} should have been deleted" }
            val themeList = workbench.givenThemeListToolHasBeenOpened()
            assertThat(themeList) {
                andThemeItemNamed(themeName) {
                    doesNotHaveSymbolNamed(symbolName)
                }
            }
        }
    }

}