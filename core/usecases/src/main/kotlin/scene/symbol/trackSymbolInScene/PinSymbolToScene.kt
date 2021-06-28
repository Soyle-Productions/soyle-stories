package com.soyle.stories.usecase.scene.symbol.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SymbolPinnedToScene
import com.soyle.stories.domain.scene.events.SymbolTrackedInScene
import com.soyle.stories.domain.theme.Symbol

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