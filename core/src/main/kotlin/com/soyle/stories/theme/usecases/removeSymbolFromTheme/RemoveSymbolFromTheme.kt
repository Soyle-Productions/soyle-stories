package com.soyle.stories.theme.usecases.removeSymbolFromTheme

import com.soyle.stories.entities.TrackedSymbolRemoved
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