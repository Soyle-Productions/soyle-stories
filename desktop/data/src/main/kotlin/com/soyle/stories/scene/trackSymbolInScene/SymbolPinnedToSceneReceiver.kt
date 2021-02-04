package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.SymbolPinnedToScene

interface SymbolPinnedToSceneReceiver {
    suspend fun receiveSymbolPinnedToScene(symbolPinnedToScene: SymbolPinnedToScene)
}