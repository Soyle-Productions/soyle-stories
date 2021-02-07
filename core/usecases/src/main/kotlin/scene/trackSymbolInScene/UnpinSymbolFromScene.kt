package com.soyle.stories.usecase.scene.trackSymbolInScene

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SymbolUnpinnedFromScene
import com.soyle.stories.domain.scene.TrackedSymbolRemoved
import com.soyle.stories.domain.theme.Symbol

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