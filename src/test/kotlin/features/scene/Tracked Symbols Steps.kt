package com.soyle.stories.desktop.config.features.scene


import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.drivers.theme.createSymbolAndThemeNamed
import com.soyle.stories.desktop.config.drivers.theme.createSymbolWithName
import com.soyle.stories.desktop.config.drivers.theme.givenCreatingNewSymbolForTheme
import com.soyle.stories.desktop.config.drivers.theme.givenCreatingNewThemeAndSymbol
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneSymbols.SymbolsInSceneAssertions
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*

class `Tracked Symbols Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I am tracking symbols in the {scene}") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
        Given(
            "I have pinned the {string} symbol from the {theme} to the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            SceneDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenSymbolPinnedInScene(scene, theme, theme.symbols.find { it.name == symbolName }!!)
        }
    }

    private fun whens() {
        When(
            "I pin the {string} symbol from the {theme} to the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)
                .pinSymbol(theme, symbolName)
        }
        When(
            "I unpin the {string} symbol from the {theme} from the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)
                .unpinSymbol(theme, symbolName)
        }
        When(
            "I create a new symbol named {string} in the {theme} to be pinned in the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenCreatingNewSymbolForTheme(theme)
                .createSymbolWithName(symbolName)
        }
        When(
            "I create a new theme named {string} and a new symbol named {string} to be pinned in the {scene}"
        ) { themeName: String, symbolName: String, scene: Scene ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            workbench.givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenCreatingNewThemeAndSymbol()
                .createSymbolAndThemeNamed(themeName, symbolName)
        }
    }

    private fun thens() {

        Then(
            "the {string} symbol for the {theme} should be tracked in the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val symbol = theme.symbols.find { it.name == symbolName }!!
            Assertions.assertTrue(scene.trackedSymbols.isSymbolTracked(symbol.id))
            assertEquals(symbol.name, scene.trackedSymbols.getSymbolById(symbol.id)!!.symbolName)

            val symbolsInSceneView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)

            SymbolsInSceneAssertions.assertThat(symbolsInSceneView) {
                hasTrackedSymbol(symbol.id, symbol.name)
            }
        }

        Then(
            "the {string} symbol from the {theme} should not be tracked in the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val symbol = theme.symbols.find { it.name == symbolName } // if null, symbol was deleted
            if (symbol == null) {
                Assertions.assertNull(scene.trackedSymbols.find { it.symbolName == symbolName })
            } else {
                assertFalse(scene.trackedSymbols.isSymbolTracked(symbol.id))
            }

            val symbolsInSceneView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)

            SymbolsInSceneAssertions.assertThat(symbolsInSceneView) {
                doesNotHaveTrackedSymbol(symbol?.id, symbolName)
            }
        }

        Then(
            "the {string} symbol from the {theme} should be pinned to the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val symbol = theme.symbols.find { it.name == symbolName }!!
            assertTrue(scene.trackedSymbols.isSymbolTracked(symbol.id))
            assertTrue(scene.trackedSymbols.getSymbolById(symbol.id)!!.isPinned)

            val symbolsInSceneView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)

            SymbolsInSceneAssertions.assertThat(symbolsInSceneView) {
                hasTrackedSymbol(symbol.id, symbolName)
                andSymbol(symbol.id) {
                    isPinned()
                }
            }
        }

        Then(
            "the {string} symbol from the {theme} should not be pinned to the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val symbol = theme.symbols.find { it.name == symbolName } // if null, symbol was deleted
            if (symbol == null) {
                assertNull(scene.trackedSymbols.find { it.symbolName == symbolName })
            } else {
                if (scene.trackedSymbols.isSymbolTracked(symbol.id)) {
                    assertFalse(scene.trackedSymbols.getSymbolById(symbol.id)!!.isPinned)
                }
            }

            val symbolsInSceneView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSymbolsInSceneToolHasBeenOpened()
                .givenFocusedOn(scene)

            SymbolsInSceneAssertions.assertThat(symbolsInSceneView) {
                if (symbol == null) {
                    doesNotHaveTrackedSymbol(null, symbolName)
                } else {
                    andSymbol(symbol.id) {
                        isNotPinned()
                    }
                }
            }
        }
    }

}