package com.soyle.stories.scene.trackSymbolInScene

import com.soyle.stories.entities.SymbolTrackedInScene

interface SymbolsTrackedInSceneReceiver {
    suspend fun receiveSymbolsTrackedInScene(symbolsTrackedInScene: List<SymbolTrackedInScene>)
}