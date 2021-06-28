package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.DetectUnusedSymbolsInScene

class DetectUnusedSymbolsOutput : Notifier<DetectUnusedSymbolsInScene.OutputPort>(),
    DetectUnusedSymbolsInScene.OutputPort {

    override suspend fun receiveDetectedUnusedSymbols(response: DetectUnusedSymbolsInScene.ResponseModel) {
        notifyAll { it.receiveDetectedUnusedSymbols(response) }
    }
}