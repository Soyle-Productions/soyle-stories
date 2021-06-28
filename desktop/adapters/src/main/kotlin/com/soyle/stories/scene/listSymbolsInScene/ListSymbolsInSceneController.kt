package com.soyle.stories.scene.listSymbolsInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.symbol.listSymbolsInScene.ListSymbolsInScene
import kotlinx.coroutines.Job

interface ListSymbolsInSceneController {
    fun listSymbolsInScene(sceneId: Scene.Id, output: ListSymbolsInScene.OutputPort): Job
}