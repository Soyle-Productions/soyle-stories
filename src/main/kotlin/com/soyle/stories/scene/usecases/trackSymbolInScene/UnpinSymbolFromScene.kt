package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.SymbolUnpinnedFromScene
import com.soyle.stories.entities.TrackedSymbolRemoved
import com.soyle.stories.entities.theme.Symbol

interface UnpinSymbolFromScene {
    suspend operator fun invoke(sceneId: Scene.Id, symbolId: Symbol.Id, output: OutputPort)

    class ResponseModel(
        val symbolUnpinnedFromScene: SymbolUnpinnedFromScene?,
        val trackedSymbolRemoved: TrackedSymbolRemoved?
    )

    interface OutputPort {
        suspend fun symbolUnpinnedFromScene(response: ResponseModel)
    }
}