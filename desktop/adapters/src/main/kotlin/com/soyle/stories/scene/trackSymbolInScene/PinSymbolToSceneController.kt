package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol

interface PinSymbolToSceneController {
    fun pinSymbolToScene(sceneId: Scene.Id, symbolId: Symbol.Id)
}