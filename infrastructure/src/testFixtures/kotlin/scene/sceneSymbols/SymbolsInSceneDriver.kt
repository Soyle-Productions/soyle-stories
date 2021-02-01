package com.soyle.stories.desktop.view.scene.sceneSymbols

import com.soyle.stories.di.get
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneState
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView.Styles.Companion.pinned
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import org.testfx.api.FxRobot
import tornadofx.hasPseudoClass

class SymbolsInSceneDriver (private val symbolsInSceneView: SymbolsInSceneView) : FxRobot() {

    fun isFocusedOn(scene: Scene): Boolean {
        return symbolsInSceneView.scope.get<SymbolsInSceneState>().targetScene.value?.id == scene.id.uuid.toString()
    }

    val symbolList: Parent?
        get() = from(symbolsInSceneView.root).lookup(".symbol-list").queryAll<Parent>().firstOrNull()

    val pinSymbolButton: MenuButton?
        get() = from(symbolsInSceneView.root).lookup(".pin-symbol-button").queryAll<MenuButton>().firstOrNull()

    fun MenuButton.themeItem(themeId: Theme.Id): Menu? {
        return items.find { it.id == themeId.toString() } as? Menu
    }

    val Menu.createSymbolOption: MenuItem
        get() = items.find { it.text == "Create New Symbol" }!!

    fun Menu.symbolItem(symbolName: String): MenuItem?
    {
        return items.find { it.text == symbolName }
    }

    fun getSymbolChip(symbolId: Symbol.Id): Label?
    {
        return symbolList?.let {
            from(it).lookup("#$symbolId").queryAll<Label>().firstOrNull()
        }
    }

    fun getSymbolChip(symbolName: String): Label?
    {
        return symbolList?.let {
            from(it).lookup(symbolName).queryAll<Label>().firstOrNull()
        }
    }

    fun Node.isPinned(): Boolean
    {
        return hasPseudoClass(pinned.name)
    }



}

fun SymbolsInSceneView.driver() = SymbolsInSceneDriver(this)
fun SymbolsInSceneView.drive(interaction: SymbolsInSceneDriver.() -> Unit) = with(driver()) { interact { interaction() } }