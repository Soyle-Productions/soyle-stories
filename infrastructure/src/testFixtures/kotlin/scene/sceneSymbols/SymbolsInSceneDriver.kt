package com.soyle.stories.desktop.view.scene.sceneSymbols

import com.soyle.stories.di.get
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneState
import com.soyle.stories.scene.sceneSymbols.SymbolsInSceneView
import javafx.scene.Node
import javafx.scene.Parent
import org.testfx.api.FxRobot

class SymbolsInSceneDriver (private val symbolsInSceneView: SymbolsInSceneView) : FxRobot() {

    fun isFocusedOn(scene: Scene): Boolean {
        return symbolsInSceneView.scope.get<SymbolsInSceneState>().targetScene.value?.id == scene.id.uuid.toString()
    }

    val symbolList: Parent?
        get() = from(symbolsInSceneView.root).lookup(".symbol-list").queryAll<Parent>().firstOrNull()

    fun getSymbolChip(expectedName: String): Node?
    {
        return from(symbolList).lookup(expectedName).queryAll<Node>().firstOrNull()
    }

}

fun SymbolsInSceneView.driver() = SymbolsInSceneDriver(this)