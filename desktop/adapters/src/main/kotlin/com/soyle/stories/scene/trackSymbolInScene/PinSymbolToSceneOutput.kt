package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.PinSymbolToScene

class PinSymbolToSceneOutput(
    private val symbolsTrackedInSceneReceiver: SymbolsTrackedInSceneReceiver,
    private val symbolPinnedToSceneReceiver: SymbolPinnedToSceneReceiver
) : PinSymbolToScene.OutputPort {
    override suspend fun symbolPinnedToScene(response: PinSymbolToScene.ResponseModel) {
        response.symbolTrackedInScene?.let { symbolsTrackedInSceneReceiver.receiveSymbolsTrackedInScene(listOf(it)) }
        response.symbolPinnedToScene?.let { symbolPinnedToSceneReceiver.receiveSymbolPinnedToScene(it) }
    }
}