package com.soyle.stories.scene.sceneSymbols

import com.soyle.stories.entities.Scene

interface SymbolsInSceneViewListener {

    fun openSceneListTool()
    fun getSymbolsInScene(sceneId: Scene.Id)

}