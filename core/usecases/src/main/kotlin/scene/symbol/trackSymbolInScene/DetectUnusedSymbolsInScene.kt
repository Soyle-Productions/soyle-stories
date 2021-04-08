package com.soyle.stories.usecase.scene.symbol.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.theme.Symbol

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