package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.Scene

interface DetectUnusedSymbolsInSceneController {
    fun detectUnusedSymbols(sceneId: Scene.Id)
}