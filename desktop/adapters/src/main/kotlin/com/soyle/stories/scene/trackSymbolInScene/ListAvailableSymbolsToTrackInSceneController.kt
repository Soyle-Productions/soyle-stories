package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.ListAvailableSymbolsToTrackInScene

interface ListAvailableSymbolsToTrackInSceneController {
    fun listAvailableSymbolsToTrackInScene(sceneId: Scene.Id, output: ListAvailableSymbolsToTrackInScene.OutputPort)
}