package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SymbolTrackedInScene

class SymbolsTrackedInSceneNotifier : Notifier<SymbolsTrackedInSceneReceiver>(), SymbolsTrackedInSceneReceiver {
    override suspend fun receiveSymbolsTrackedInScene(symbolsTrackedInScene: List<SymbolTrackedInScene>) {
        notifyAll { it.receiveSymbolsTrackedInScene(symbolsTrackedInScene) }
    }
}