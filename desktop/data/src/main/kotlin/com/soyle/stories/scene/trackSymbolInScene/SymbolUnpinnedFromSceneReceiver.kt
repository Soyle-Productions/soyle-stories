package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.SymbolUnpinnedFromScene

interface SymbolUnpinnedFromSceneReceiver {
    suspend fun receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene: SymbolUnpinnedFromScene)
}