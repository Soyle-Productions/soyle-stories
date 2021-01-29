package com.soyle.stories.desktop.view.scene.sceneSymbols

import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull

class SymbolsInSceneAssertions private constructor(private val driver: SymbolsInSceneDriver) {

    companion object {
        fun assertThat(symbolsInSceneView: SymbolsInSceneView, assertions: SymbolsInSceneAssertions.() -> Unit)
        {
            SymbolsInSceneAssertions(SymbolsInSceneDriver(symbolsInSceneView)).assertions()
        }
    }

    fun hasTrackedSymbol(themeId: Theme.Id, expectedThemeName: String, symbolId: Symbol.Id, expectedSymbolName: String) {
        assertNotNull(driver.getSymbolChip(expectedSymbolName))
    }

    fun doesNotHaveTrackedSymbol(themeId: Theme.Id, expectedThemeName: String, symbolId: Symbol.Id?, expectedSymbolName: String) {
        assertNull(driver.getSymbolChip(expectedSymbolName))
    }

}