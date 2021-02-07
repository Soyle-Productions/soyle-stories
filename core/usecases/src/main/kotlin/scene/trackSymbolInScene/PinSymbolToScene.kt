package com.soyle.stories.usecase.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SymbolPinnedToScene
import com.soyle.stories.domain.scene.SymbolTrackedInScene
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