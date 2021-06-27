package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.theme.Symbol

interface PinSymbolToSceneController {
    fun pinSymbolToScene(sceneId: Scene.Id, symbolId: Symbol.Id)
}