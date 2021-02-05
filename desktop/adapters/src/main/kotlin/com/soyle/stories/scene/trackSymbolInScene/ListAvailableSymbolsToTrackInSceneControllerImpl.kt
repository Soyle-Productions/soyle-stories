package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.usecases.trackSymbolInScene.ListAvailableSymbolsToTrackInScene

class ListAvailableSymbolsToTrackInSceneControllerImpl(
    private val threadTransformer: ThreadTransformer,
    private val listAvailableSymbolsToTrackInScene: ListAvailableSymbolsToTrackInScene
) : ListAvailableSymbolsToTrackInSceneController {
    override fun listAvailableSymbolsToTrackInScene(
        sceneId: Scene.Id,
        output: ListAvailableSymbolsToTrackInScene.OutputPort
    ) {
        threadTransformer.async {
            listAvailableSymbolsToTrackInScene.invoke(sceneId, output)
        }
    }
}