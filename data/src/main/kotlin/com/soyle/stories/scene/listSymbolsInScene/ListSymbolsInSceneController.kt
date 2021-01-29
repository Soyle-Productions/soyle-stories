package com.soyle.stories.scene.listSymbolsInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.usecases.listSymbolsInScene.ListSymbolsInScene

interface ListSymbolsInSceneController {
    fun listSymbolsInScene(sceneId: Scene.Id, output: ListSymbolsInScene.OutputPort)
}