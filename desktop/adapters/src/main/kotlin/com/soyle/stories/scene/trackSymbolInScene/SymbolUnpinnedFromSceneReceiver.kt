package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.SymbolUnpinnedFromScene

interface SymbolUnpinnedFromSceneReceiver {
    suspend fun receiveSymbolUnpinnedFromScene(symbolUnpinnedFromScene: SymbolUnpinnedFromScene)
}