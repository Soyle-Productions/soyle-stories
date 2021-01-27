package com.soyle.stories.scene.usecases.trackSymbolInScene

import com.soyle.stories.entities.Prose
import com.soyle.stories.entities.SymbolTrackedInScene
import com.soyle.stories.entities.TrackedSymbolRemoved

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