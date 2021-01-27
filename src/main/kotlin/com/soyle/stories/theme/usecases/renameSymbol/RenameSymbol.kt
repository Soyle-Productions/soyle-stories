package com.soyle.stories.theme.usecases.renameSymbol

import com.soyle.stories.entities.TrackedSymbolRenamed
import java.util.*

interface RenameSymbol {

    suspend operator fun invoke(symbolId: UUID, name: String, output: OutputPort)

    class ResponseModel(
        val renamedSymbol: RenamedSymbol,
        val trackedSymbolsRenamed: List<TrackedSymbolRenamed>
    )

    interface OutputPort {
        suspend fun symbolRenamed(response: ResponseModel)
    }

}