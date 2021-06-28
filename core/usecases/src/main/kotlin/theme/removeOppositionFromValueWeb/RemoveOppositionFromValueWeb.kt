package com.soyle.stories.usecase.theme.removeOppositionFromValueWeb

import java.util.*

interface RemoveOppositionFromValueWeb {

    suspend operator fun invoke(oppositionId: UUID, valueWebId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun removedOppositionFromValueWeb(response: OppositionRemovedFromValueWeb)
    }

}