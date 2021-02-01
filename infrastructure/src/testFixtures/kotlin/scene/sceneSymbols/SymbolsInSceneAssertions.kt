package com.soyle.stories.desktop.view.scene.sceneSymbols

import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView
import javafx.scene.control.Label
import org.junit.jupiter.api.Assertions.*

class SymbolsInSceneAssertions private constructor(private val driver: SymbolsInSceneDriver) {

    companion object {
        fun assertThat(symbolsInSceneView: SymbolsInSceneView, assertions: SymbolsInSceneAssertions.() -> Unit)
        {
            SymbolsInSceneAssertions(SymbolsInSceneDriver(symbolsInSceneView)).assertions()
        }
    }

    fun hasTrackedSymbol(symbolId: Symbol.Id, expectedSymbolName: String) {
        val symbolChip = driver.getSymbolChip(symbolId)!!
        assertEquals(expectedSymbolName, symbolChip.text)
    }

    fun doesNotHaveTrackedSymbol(symbolId: Symbol.Id?, expectedSymbolName: String) {
        if (symbolId != null) {
            assertNull(driver.getSymbolChip(symbolId))
        }
        assertNull(driver.getSymbolChip(expectedSymbolName))
    }

    fun andSymbol(symbolId: Symbol.Id, assertions: SymbolChipAssertions.() -> Unit)
    {
        SymbolChipAssertions(driver.getSymbolChip(symbolId)!!).assertions()
    }

    inner class SymbolChipAssertions internal constructor(private val chip: Label) {
        fun isPinned() {
            assertTrue(with(driver) { chip.isPinned() })
        }
        fun isNotPinned() {
            assertFalse(with(driver) { chip.isPinned() })
        }
    }

}