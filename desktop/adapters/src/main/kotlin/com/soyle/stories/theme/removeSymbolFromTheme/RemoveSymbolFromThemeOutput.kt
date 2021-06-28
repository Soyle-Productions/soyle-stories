package com.soyle.stories.theme.removeSymbolFromTheme

import com.soyle.stories.scene.trackSymbolInScene.TrackedSymbolsRemovedReceiver
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.RemoveSymbolFromTheme

class RemoveSymbolFromThemeOutput(
    private val symbolRemovedFromThemeReceiver: SymbolRemovedFromThemeReceiver,
    private val trackedSymbolsRemovedReceiver: TrackedSymbolsRemovedReceiver
) : RemoveSymbolFromTheme.OutputPort {

    override suspend fun removedSymbolFromTheme(response: RemoveSymbolFromTheme.ResponseModel) {
        symbolRemovedFromThemeReceiver.receiveSymbolRemovedFromTheme(response.symbolRemovedFromTheme)
        if (response.trackedSymbolsRemoved.isNotEmpty()) {
            trackedSymbolsRemovedReceiver.receiveTrackedSymbolsRemoved(response.trackedSymbolsRemoved)
        }
    }
}