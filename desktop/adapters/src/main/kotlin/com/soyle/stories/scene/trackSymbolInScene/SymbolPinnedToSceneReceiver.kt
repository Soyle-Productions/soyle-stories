package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.events.SymbolPinnedToScene

interface SymbolPinnedToSceneReceiver {
    suspend fun receiveSymbolPinnedToScene(symbolPinnedToScene: SymbolPinnedToScene)
}