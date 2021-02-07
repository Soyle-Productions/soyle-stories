package com.soyle.stories.usecase.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.theme.listSymbolsByTheme.SymbolsByTheme

interface ListAvailableSymbolsToTrackInScene {
    suspend operator fun invoke(sceneId: Scene.Id, output: OutputPort)

    interface OutputPort {
        suspend fun receiveAvailableSymbolsToTrackInScene(response: SymbolsByTheme)
    }
}