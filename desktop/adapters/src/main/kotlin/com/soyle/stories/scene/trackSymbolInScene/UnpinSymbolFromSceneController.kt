package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol

interface UnpinSymbolFromSceneController {
    fun unpinSymbolFromScene(sceneId: Scene.Id, symbolId: Symbol.Id)
}