package com.soyle.stories.usecase.scene.symbol.trackSymbolInScene

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.events.SymbolTrackedInScene
import com.soyle.stories.domain.scene.events.TrackedSymbolRemoved

interface SynchronizeTrackedSymbolsWithProse {
    suspend operator fun invoke(proseId: Prose.Id, output: OutputPort)

    class ResponseModel(
        val symbolsTrackedInScene: List<SymbolTrackedInScene>,
        val symbolsNoLongerTrackedInScene: List<TrackedSymbolRemoved>
    )

    interface OutputPort {
        suspend fun symbolTrackedInScene(response: ResponseModel)
    }

}