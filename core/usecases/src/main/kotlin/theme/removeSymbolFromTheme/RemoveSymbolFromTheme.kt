package com.soyle.stories.usecase.theme.removeSymbolFromTheme

import com.soyle.stories.domain.scene.events.TrackedSymbolRemoved
import java.util.*

interface RemoveSymbolFromTheme {

    suspend operator fun invoke(symbolId: UUID, output: OutputPort)

    class ResponseModel(
        val symbolRemovedFromTheme: SymbolRemovedFromTheme,
        val trackedSymbolsRemoved: List<TrackedSymbolRemoved>
    )

    interface OutputPort {
        suspend fun removedSymbolFromTheme(response: ResponseModel)
    }

}