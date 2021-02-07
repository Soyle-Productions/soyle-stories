package com.soyle.stories.usecase.scene.trackSymbolInScene

import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.SymbolTrackedInScene
import com.soyle.stories.domain.scene.TrackedSymbolRemoved

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