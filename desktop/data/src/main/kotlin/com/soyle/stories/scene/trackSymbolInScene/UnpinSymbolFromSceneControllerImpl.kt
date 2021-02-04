package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.scene.usecases.trackSymbolInScene.UnpinSymbolFromScene

class UnpinSymbolFromSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val unpinSymbolFromScene: UnpinSymbolFromScene,
    private val unpinSymbolFromSceneOutput: UnpinSymbolFromScene.OutputPort
) : UnpinSymbolFromSceneController {
    override fun unpinSymbolFromScene(sceneId: Scene.Id, symbolId: Symbol.Id) {
        threadTransformer.async {
            unpinSymbolFromScene.invoke(sceneId, symbolId, unpinSymbolFromSceneOutput)
        }
    }
}