package com.soyle.stories.desktop.config.features.scene


import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse

class `Tracked Symbols Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {

    }

    private fun whens() {

    }

    private fun thens() {

        Then(
            "the {string} symbol for the {theme} should be tracked in the {scene}"
        ) { symbolName: String, theme: Theme, scene: Scene ->
            val symbol = theme.symbols.find { it.name == symbolName }!!
            Assertions.assertTrue(scene.trackedSymbols.isSymbolTracked(symbol.id))
            assertEquals(symbol.name, scene.trackedSymbols.getSymbolById(symbol.id)!!.symbolName)

            TODO("Ensure scene symbol tool has updated")
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

            TODO("Ensure scene symbol tool has updated")
        }
    }

}