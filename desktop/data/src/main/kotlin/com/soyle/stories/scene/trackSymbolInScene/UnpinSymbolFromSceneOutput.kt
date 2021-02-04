package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.scene.usecases.trackSymbolInScene.UnpinSymbolFromScene

class UnpinSymbolFromSceneOutput(
    private val trackedSymbolsRemovedReceiver: TrackedSymbolsRemovedReceiver,
    private val symbolUnpinnedFromSceneReceiver: SymbolUnpinnedFromSceneReceiver
) : UnpinSymbolFromScene.OutputPort {
    override suspend fun symbolUnpinnedFromScene(response: UnpinSymbolFromScene.ResponseModel) {
        response.symbolUnpinnedFromScene?.let { symbolUnpinnedFromSceneReceiver.receiveSymbolUnpinnedFromScene(it) }
        response.trackedSymbolRemoved?.let { trackedSymbolsRemovedReceiver.receiveTrackedSymbolsRemoved(listOf(it)) }
    }
}