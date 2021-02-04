package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme

interface ListAvailableSymbolsToTrackInScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableSymbolsToTrackInScene(response: SymbolsByTheme)
    }
}