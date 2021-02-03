package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.theme.Symbol

interface UnpinSymbolFromSceneController {
    fun unpinSymbolFromScene(sceneId: Scene.Id, symbolId: Symbol.Id)
}