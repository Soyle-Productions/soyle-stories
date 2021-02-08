package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.SymbolTrackedInScene

interface SymbolsTrackedInSceneReceiver {
    suspend fun receiveSymbolsTrackedInScene(symbolsTrackedInScene: List<SymbolTrackedInScene>)
}