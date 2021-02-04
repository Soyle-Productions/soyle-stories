package com.soyle.stories.theme.usecases.renameSymbolicItems

import java.util.*

interface RenameSymbolicItem {

    suspend operator fun invoke(symbolicEntityId: UUID, newName: String, output: OutputPort)

    interface OutputPort {
        suspend fun symbolicItemRenamed(response: List<RenamedSymbolicItem>)
    }

}