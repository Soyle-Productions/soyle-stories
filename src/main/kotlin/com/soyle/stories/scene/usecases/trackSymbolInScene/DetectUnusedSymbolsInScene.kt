package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.theme.Symbol

interface DetectUnusedSymbolsInScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    class ResponseModel(
        val sceneId: Scene.Id,
        val unusedSymbolIds: Set<Symbol.Id>
    )

    interface OutputPort {
        suspend fun receiveDetectedUnusedSymbols(response: ResponseModel)
    }
}