package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.SymbolPinnedToScene
import com.soyle.stories.entities.SymbolTrackedInScene
import com.soyle.stories.entities.theme.Symbol

interface PinSymbolToScene {
    suspend operator fun invoke(sceneId: Scene.Id, symbolId: Symbol.Id, output: OutputPort)

    class ResponseModel(
        val symbolPinnedToScene: SymbolPinnedToScene?,
        val symbolTrackedInScene: SymbolTrackedInScene?
    )

    interface OutputPort {
        suspend fun symbolPinnedToScene(response: ResponseModel)
    }
}