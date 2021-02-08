package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.trackSymbolInScene.UnpinSymbolFromScene

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