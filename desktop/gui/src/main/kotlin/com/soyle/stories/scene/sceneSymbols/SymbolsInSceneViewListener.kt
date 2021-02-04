package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.theme.Symbol

interface SymbolsInSceneViewListener {

    fun openSceneListTool()
    fun getSymbolsInScene(sceneId: Scene.Id)
    fun listAvailableSymbolsToTrack(sceneId: Scene.Id)
    fun pinSymbol(sceneId: Scene.Id, symbolId: Symbol.Id)
    fun unpinSymbol(sceneId: Scene.Id, symbolId: Symbol.Id)

}