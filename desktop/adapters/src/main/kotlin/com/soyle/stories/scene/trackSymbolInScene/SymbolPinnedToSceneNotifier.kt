package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.common.Notifier
import com.soyle.stories.domain.scene.events.SymbolPinnedToScene

class SymbolPinnedToSceneNotifier : Notifier<SymbolPinnedToSceneReceiver>(), SymbolPinnedToSceneReceiver {
    override suspend fun receiveSymbolPinnedToScene(symbolPinnedToScene: SymbolPinnedToScene) {
        notifyAll { it.receiveSymbolPinnedToScene(symbolPinnedToScene) }
    }
}