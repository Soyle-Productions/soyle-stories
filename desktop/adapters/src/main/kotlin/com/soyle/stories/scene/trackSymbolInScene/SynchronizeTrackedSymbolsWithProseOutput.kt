package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.usecase.scene.symbol.trackSymbolInScene.SynchronizeTrackedSymbolsWithProse

class SynchronizeTrackedSymbolsWithProseOutput(
    private val symbolsTrackedInSceneReceiver: SymbolsTrackedInSceneReceiver,
    private val trackedSymbolsRemovedReceiver: TrackedSymbolsRemovedReceiver
) : SynchronizeTrackedSymbolsWithProse.OutputPort {
    override suspend fun symbolTrackedInScene(response: SynchronizeTrackedSymbolsWithProse.ResponseModel) {
        if (response.symbolsTrackedInScene.isNotEmpty()) symbolsTrackedInSceneReceiver.receiveSymbolsTrackedInScene(
            response.symbolsTrackedInScene
        )
        if (response.symbolsNoLongerTrackedInScene.isNotEmpty()) trackedSymbolsRemovedReceiver.receiveTrackedSymbolsRemoved(
            response.symbolsNoLongerTrackedInScene
        )
    }
}