package com.soyle.stories.usecase.theme.removeSymbolicItem

import java.util.*

interface RemoveSymbolicItem {

    suspend fun removeSymbolicItemFromOpposition(oppositionId: UUID, symbolicItemId: UUID, output: OutputPort)
    suspend fun removeSymbolicItemFromAllThemes(symbolicItemId: UUID, output: OutputPort)

    interface OutputPort {

        suspend fun symbolicItemsRemoved(response: List<RemovedSymbolicItem>)

    }

}