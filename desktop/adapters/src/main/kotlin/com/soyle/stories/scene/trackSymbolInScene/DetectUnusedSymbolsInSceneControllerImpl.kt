package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.DetectUnusedSymbolsInScene

class DetectUnusedSymbolsInSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val detectUnusedSymbolsInScene: DetectUnusedSymbolsInScene,
    private val detectUnusedSymbolsInSceneOutput: DetectUnusedSymbolsInScene.OutputPort
) : DetectUnusedSymbolsInSceneController {

    override fun detectUnusedSymbols(sceneId: Scene.Id) {
        threadTransformer.async {
            detectUnusedSymbolsInScene.invoke(sceneId, detectUnusedSymbolsInSceneOutput)
        }
    }
}