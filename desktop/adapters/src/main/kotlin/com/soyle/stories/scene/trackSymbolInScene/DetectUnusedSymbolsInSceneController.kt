package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene

interface DetectUnusedSymbolsInSceneController {
    fun detectUnusedSymbols(sceneId: Scene.Id)
}