package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.usecase.scene.trackSymbolInScene.PinSymbolToScene

class PinSymbolToSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val pinSymbolToScene: PinSymbolToScene,
    private val pinSymbolToSceneOutput: PinSymbolToScene.OutputPort
) : PinSymbolToSceneController {

    override fun pinSymbolToScene(sceneId: Scene.Id, symbolId: Symbol.Id) {
        threadTransformer.async {
            pinSymbolToScene.invoke(sceneId, symbolId, pinSymbolToSceneOutput)
        }
    }

}