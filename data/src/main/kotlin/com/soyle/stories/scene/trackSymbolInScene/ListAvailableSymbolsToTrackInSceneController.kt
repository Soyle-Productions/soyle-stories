package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.usecases.trackSymbolInScene.ListAvailableSymbolsToTrackInScene

interface ListAvailableSymbolsToTrackInSceneController {
    fun listAvailableSymbolsToTrackInScene(sceneId: Scene.Id, output: ListAvailableSymbolsToTrackInScene.OutputPort)
}