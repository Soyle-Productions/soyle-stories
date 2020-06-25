package com.soyle.stories.theme.usecases.renameSymbol

import java.util.*

interface RenameSymbol {

    suspend operator fun invoke(symbolId: UUID, name: String, output: OutputPort)

    interface OutputPort {
        suspend fun symbolRenamed(response: RenamedSymbol)
    }

}