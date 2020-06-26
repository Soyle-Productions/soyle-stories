package com.soyle.stories.theme.usecases.addOppositionToValueWeb

import java.util.*

interface AddOppositionToValueWeb {

    suspend operator fun invoke(valueWebId: UUID, output: OutputPort)

    interface OutputPort {
        suspend fun addedOppositionToValueWeb(response: OppositionAddedToValueWeb)
    }

}