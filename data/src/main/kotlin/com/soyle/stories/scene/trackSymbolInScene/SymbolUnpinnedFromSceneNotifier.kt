package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.entities.SymbolUnpinnedFromScene

class SymbolUnpinnedFromSceneNotifier : Notifier<SymbolUnpinnedFromSceneReceiver>(), SymbolUnpinnedFromSceneReceiver {
    override suspend fun receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene: SymbolUnpinnedFromScene) {
        notifyAll { it.receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene) }
    }
}